/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.param;

import com.github.ykrasik.jemi.cli.CliConstants;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.cli.assist.BoundParams;
import com.github.ykrasik.jemi.cli.assist.CliValueType;
import com.github.ykrasik.jemi.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.command.CommandArgs;
import com.github.ykrasik.jemi.command.CommandArgsImpl;
import com.github.ykrasik.jemi.util.function.Predicate;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.trie.Trie;
import com.github.ykrasik.jemi.util.trie.Tries;
import lombok.NonNull;

import java.util.*;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class CliParamParseContext {
    private static final CliValueType.Mapper<CliParam> PARAM_NAME_MAPPER = new CliValueType.Mapper<>(CliValueType.COMMAND_PARAM_NAME);

    private final List<CliParam> params;
    private final Trie<CliParam> paramsTrie;

    private final Map<CliParam, Object> args;
    private final Queue<CliParam> unboundParams;

    private Opt<CliParam> nextNamedParam = Opt.absent();

    // TODO: JavaDoc
    public CliParamParseContext(@NonNull List<CliParam> params, @NonNull Trie<CliParam> paramsTrie) {
        this.params = params;
        this.paramsTrie = paramsTrie;

        this.args = new HashMap<>(params.size());
        this.unboundParams = new LinkedList<>(params);
    }

    // TODO: JavaDoc
    public void parseValue(String rawValue) throws ParseException {
        if (!rawValue.startsWith(CliConstants.NAMED_PARAM_PREFIX)) {
            // Value doesn't start with '-', have the next param parse it.
            parseNextArg(rawValue);
            return;
        }

        // Value starts with a '-', what comes after is expected to be a valid name of a parameter.
        // Unless, and this is a corner case, the first character of the name is a number, which means we're
        // passing a negative number as a value.
        // Param names cannot start with a number.
        final String paramName = rawValue.substring(1);
        if (paramName.isEmpty()) {
            throw new ParseException(ParseError.INVALID_PARAM, "No parameter name specified after '%s'!", CliConstants.NAMED_PARAM_PREFIX);
        }

        if (Character.isDigit(paramName.charAt(0))) {
            // This value is a negative number, have the next param parse it.
            parseNextArg(rawValue);
        } else {
            // rawValue starts with '-' and is the name of a parameter, set the next expected named parameter.
            setNextNamedParam(paramName);
        }
    }

    private void setNextNamedParam(String paramName) throws ParseException {
        nextNamedParam = paramsTrie.get(paramName);
        if (!nextNamedParam.isPresent()) {
            throw new ParseException(ParseError.INVALID_PARAM, "Invalid parameter name: '%s'", paramName);
        }
    }

    private void parseNextArg(String rawValue) throws ParseException {
        if (nextNamedParam.isPresent()) {
            // The prev param was the name of a param, the param specified by that name should parse the arg.
            parseNextNamedParam(rawValue);
        } else {
            // Use the next unbound positional param to parse the arg.
            parseNextPositionalParam(rawValue);
        }
    }

    private void parseNextPositionalParam(String rawValue) throws ParseException {
        // Use the next unbound positional param to parse the arg.
        final CliParam param = getNextUnboundParam(rawValue);
        final Object arg = param.parse(rawValue);
        addArg(param, arg);
    }

    private CliParam getNextUnboundParam(String rawValue) throws ParseException {
        final CliParam param = unboundParams.peek();
        if (param == null) {
            throw new ParseException(ParseError.NO_MORE_PARAMS, "Excess argument: '%s'", rawValue);
        }
        return param;
    }

    private void parseNextNamedParam(String rawValue) throws ParseException {
        if (!nextNamedParam.isPresent()) {
            throw new IllegalStateException(String.format("Internal Error: Named parameter wasn't set: '%s'", rawValue));
        }

        // The prev param was the name of a param, the param specified by that name should parse the arg.
        final CliParam param = nextNamedParam.get();
        final Object arg = parseNamedParam(param, rawValue);
        addArg(param, arg);
        nextNamedParam = Opt.absent();
    }

    private Object parseNamedParam(CliParam param, String rawValue) throws ParseException {
        try {
            return param.parse(rawValue);
        } catch (ParseException e) {
            // Try recovering with a fallback by having the param parse a 'no value' value.
            // This can only succeed with very specific parameters and very specific cases.
            try {
                return param.noValue();
            } catch (ParseException e2) {
                // Throw the original exception if the fallback failed.
                throw e;
            }
        }
    }

    // TODO: JavaDoc
    public CommandArgs createCommandArgs() throws ParseException {
        // In case the last parsed arg ended with '-{paramName}', have that parameter parse a 'no-value' value.
        // Can only succeed in certain cases with certain parameters.
        if (nextNamedParam.isPresent()) {
            // The last parsed arg did indeed end with '-{paramName}' without assigning that parameter a value.
            final CliParam param = nextNamedParam.get();
            final Object arg = param.noValue();
            addArg(param, arg);
        }

        // Assign default values to any optional params not bound.
        resolveUnboundParams();

        final List<Object> commandArgs = new ArrayList<>(params.size());
        for (CliParam param : params) {
            final Object arg = args.get(param);
            if (arg == null) {
                throw new IllegalStateException(String.format("Internal Error: Not all params have been parsed! Missing=" + param));
            }
            commandArgs.add(arg);
        }
        return new CommandArgsImpl(commandArgs);
    }

    private void resolveUnboundParams() throws ParseException {
        final Iterator<CliParam> iterator = unboundParams.iterator();
        while (iterator.hasNext()) {
            final CliParam param = iterator.next();
            final Object arg = param.unbound();
            doAddArg(param, arg);

            // Param is no longer unbound.
            iterator.remove();
        }
    }

    // TODO: JavaDoc
    public ParamAssistInfo createParamAssistInfo(String prefix) throws ParseException {
        if (nextNamedParam.isPresent()) {
            // The last parsed value was of the form '-{paramName}'.
            // Have that named parameter auto-complete the prefix.
            final CliParam param = nextNamedParam.get();
            final AutoComplete autoComplete = param.autoComplete(prefix);
            final BoundParams boundParams = new BoundParams(args, nextNamedParam);
            return new ParamAssistInfo(boundParams, autoComplete);
        }

        // Check if 'prefix' starts with the named parameter call prefix.
        final Opt<CliParam> nextParam = Opt.of(getNextUnboundParam(prefix));
        final AutoComplete autoComplete;
        if (prefix.startsWith(CliConstants.NAMED_PARAM_PREFIX)) {
            // If it does, prefix can only represent a param name.
            // TODO: Or a negative number... which cannot be auto-completed.
            final String paramNamePrefix = prefix.substring(1);
            autoComplete = autoCompleteParamName(paramNamePrefix);
        } else {
            // If it doesn't, prefix can be either the name of a param or the value of the next positional param.
            autoComplete = autoCompleteParamNameOrValue(prefix);
        }
        final BoundParams boundParams = new BoundParams(args, nextParam);
        return new ParamAssistInfo(boundParams, autoComplete);
    }

    private AutoComplete autoCompleteParamNameOrValue(String prefix) throws ParseException {
        // Prefix can either be the value of the next positional param or the name of any unbound param.
        // However, there are 2 things that we don't want to do:
        //  1. Offer to autoComplete the name of the last unbound param (just go straight to it's value).
        //  2. Mask autoComplete errors in the absence of other autoComplete possibilities.

        // Try to autoComplete prefix as the name of any unbound param.
        // TODO: Do offer to autoComplete the name of the last unbound parameter, but only if no other choice.
        final AutoComplete paramNameAutoComplete = autoCompleteParamName(prefix);

        // AutoCompleting the param value could throw an exception, which we don't want to mask
        // if we don't have any other autoComplete possibilities available.
        try {
            final CliParam nextPositionalParam = getNextUnboundParam(prefix);
            final AutoComplete paramValueAutoComplete = nextPositionalParam.autoComplete(prefix);
            return paramNameAutoComplete.union(paramValueAutoComplete);
        } catch (ParseException e) {
            if (!paramNameAutoComplete.isEmpty()) {
                return paramNameAutoComplete;
            } else {
                throw e;
            }
        }
    }

    private AutoComplete autoCompleteParamName(String prefix) {
        final Trie<CliValueType> paramNamePossibilities;
        if (unboundParams.size() == 1) {
            // Don't suggest param names if there is only 1 option available.
            paramNamePossibilities = Tries.emptyTrie();
        } else {
            final Trie<CliParam> prefixParams = paramsTrie.subTrie(prefix);
            final Trie<CliParam> unboundPrefixParams = prefixParams.filter(new Predicate<CliParam>() {
                @Override
                public boolean test(CliParam value) {
                    // Only keep unbound params.
                    return !args.containsKey(value);
                }
            });
            paramNamePossibilities = unboundPrefixParams.mapValues(PARAM_NAME_MAPPER);
        }
        return new AutoComplete(prefix, paramNamePossibilities);
    }

    private void addArg(CliParam param, Object arg) throws ParseException {
        doAddArg(param, arg);
        if (!unboundParams.remove(param)) {
            throw new IllegalStateException(String.format("Internal Error: Param bound to value wasn't previously unbound: param=%s, value='%s'", param.getIdentifier().getName(), arg));
        }
    }

    private void doAddArg(CliParam param, Object arg) throws ParseException {
        final Object prevArg = args.put(param, arg);
        if (prevArg != null) {
            throw new ParseException(ParseError.PARAM_ALREADY_BOUND, "Parameter '%s' is already bound a value: '%s'", param.getIdentifier().getName(), prevArg);
        }
    }
}

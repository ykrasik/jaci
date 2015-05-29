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
import com.github.ykrasik.jemi.cli.assist.CliValueType;
import com.github.ykrasik.jemi.cli.assist.CliValueTypeMapper;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.command.CliCommandArgs;
import com.github.ykrasik.jemi.cli.command.CliCommandArgsImpl;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.util.function.Function;
import com.github.ykrasik.jemi.util.function.Predicate;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.trie.Trie;
import com.github.ykrasik.jemi.util.trie.TrieBuilder;
import com.github.ykrasik.jemi.util.trie.Tries;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@ToString(of = "params")
public class CliParamManagerImpl implements CliParamManager {
    private final List<CliParam> params;
    private final Trie<CliParam> paramsTrie;

    public CliParamManagerImpl(@NonNull List<CliParam> params) {
        this.params = params;
        this.paramsTrie = createParamsTrie(params);
    }

    private Trie<CliParam> createParamsTrie(List<CliParam> params) {
        final TrieBuilder<CliParam> builder = new TrieBuilder<>();
        for (CliParam param : params) {
            builder.add(param.getIdentifier().getName(), param);
        }
        return builder.build();
    }

    @Override
    public CliCommandArgs parse(List<String> args) throws ParseException {
        // Parse all args.
        final ParseContext context = doParse(args);

        // args ended with '-{paramName}', without assigning that parameter a value.
        if (context.nextNamedParam.isPresent()) {
            final CliParam param = context.nextNamedParam.get();
            final CliArg arg = parseNoValueParam(param);
            context.addArg(param, arg);
        }

        // Assign default values to any optional params not passed.
        for (CliParam param : context.unboundParams) {
            final CliArg arg = parseUnboundParam(param);
            context.addArg(param, arg);
        }

        return new CliCommandArgsImpl();
    }

    private ParseContext doParse(List<String> args) throws ParseException {
        final ParseContext context = new ParseContext();
        for (String arg : args) {
            parseValue(context, arg);
        }
        return context;
    }

    private void parseValue(ParseContext context, String rawValue) throws ParseException {
        if (!rawValue.startsWith(CliConstants.NAMED_PARAM_PREFIX)) {
            // Value doesn't start with '-', have the next param parse it.
            context.parseNextArg(rawValue);
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
            context.parseNextArg(rawValue);
        } else {
            // rawValue starts with '-' and is the name of a parameter, set the context accordingly.
            context.setNextNamedParam(paramName);
        }
    }

    private CliArg parseParam(CliParam param, String rawValue) throws ParseException {
        final Object parsedValue = param.parse(rawValue);
        return new CliArg(Opt.of(rawValue), parsedValue);
    }

    private CliArg parseNoValueParam(CliParam param) throws ParseException {
        // Have the param parse a 'no value' value.
        // This can only succeed with very specific parameters and very specific cases.
        final Object fallbackValue = param.noValue();
        return new CliArg(Opt.<String>absent(), fallbackValue);
    }

    private CliArg parseUnboundParam(CliParam param) throws ParseException {
        final Object defaultValue = param.unbound();
        return new CliArg(Opt.<String>absent(), defaultValue);
    }

    private CliArg parseNamedParam(CliParam param, String rawValue) throws ParseException {
        try {
            return parseParam(param, rawValue);
        } catch (ParseException e) {
            // Try recovering with a fallback by having the param parse a 'no value' value.
            // This only applies to very specific parameters for very specific cases.
            try {
                return parseNoValueParam(param);
            } catch (ParseException e2) {
                // Throw the original exception if the fallback failed.
                throw e;
            }
        }
    }

    @Override
    public AutoComplete assist(List<String> args) throws ParseException {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        // Parse all params that have been bound.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final String prefix = args.get(args.size() - 1);

        final ParseContext context = doParse(argsToBeParsed);

        // args ended with '-{paramName}'. Have that named parameter auto-complete the prefix.
        if (context.nextNamedParam.isPresent()) {
            final CliParam param = context.nextNamedParam.get();
            return param.autoComplete(prefix);
        }

        // Check if 'prefix' starts with the named parameter call prefix.
        if (prefix.startsWith(CliConstants.NAMED_PARAM_PREFIX)) {
            // If it does, prefix can only represent a param name.
            final String paramNamePrefix = prefix.substring(1);
            return autoCompleteParamName(context, paramNamePrefix);
        } else {
            // If it doesn't, prefix can be either the name of a param or the value of the next positional param.
            return autoCompleteParamNameOrValue(context, prefix);
        }
    }

    private AutoComplete autoCompleteParamNameOrValue(ParseContext context, String prefix) throws ParseException {
        // Prefix can either be the value of the next positional param or the name of any unbound param.
        // However, there are 2 things that we don't want to do:
        //  1. Offer to autoComplete the name of the last unbound param (just go straight to it's value).
        //  2. Mask autoComplete errors in the absence of other autoComplete possibilities.

        // Try to autoComplete prefix as the name of any unbound param.
        // TODO: Do offer to autoComplete the name of the last unbound parameter, but only if no other choice.
        final AutoComplete paramNameAutoComplete = autoCompleteParamName(context, prefix);

        // AutoCompleting the param value could throw an exception, which we don't want to mask
        // if we don't have any other autoComplete possibilities available.
        try {
            final CliParam nextPositionalParam = context.getNextUnboundParam(prefix);
            final AutoComplete paramValueAutoComplete = nextPositionalParam.autoComplete(prefix);
            if (paramNameAutoComplete.isEmpty()) {
                return paramValueAutoComplete;
            } else {
                return paramNameAutoComplete.union(paramValueAutoComplete);
            }
        } catch (ParseException e) {
            if (!paramNameAutoComplete.isEmpty()) {
                return paramNameAutoComplete;
            } else {
                throw e;
            }
        }
    }

    private AutoComplete autoCompleteParamName(ParseContext context, String prefix) {
        final Trie<CliValueType> paramNamePossibilities;
        if (context.unboundParams.size() == 1) {
            // Don't suggest param names if there is only 1 option available.
            paramNamePossibilities = Tries.emptyTrie();
        } else {
            final Trie<CliParam> prefixParams = paramsTrie.subTrie(prefix);
            final Trie<CliParam> unboundParams = prefixParams.filter(context.unboundParamPredicate());
            paramNamePossibilities = unboundParams.mapValues(PARAM_NAME_MAPPER);
        }
        return new AutoComplete(prefix, paramNamePossibilities);
    }

    private class ParseContext {
        private final Map<CliParam, CliArg> args = new HashMap<>(params.size());
        private final Queue<CliParam> unboundParams = new LinkedList<>(params);

        private Opt<CliParam> nextNamedParam = Opt.absent();

        public void setNextNamedParam(String paramName) throws ParseException {
            nextNamedParam = paramsTrie.get(paramName);
            if (!nextNamedParam.isPresent()) {
                throw new ParseException(ParseError.INVALID_PARAM, "Invalid parameter name: '%s'", paramName);
            }
        }

        public void parseNextArg(String rawValue) throws ParseException {
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
            final CliArg arg = parseParam(param, rawValue);
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
                throw new ParseException(ParseError.INTERNAL_ERROR, "Internal Error: Named parameter wasn't set: '%s'", rawValue);
            }

            // The prev param was the name of a param, the param specified by that name should parse the arg.
            final CliParam param = nextNamedParam.get();
            final CliArg arg = parseNamedParam(param, rawValue);
            addArg(param, arg);
            nextNamedParam = Opt.absent();
        }

        private void addArg(CliParam param, CliArg arg) throws ParseException {
            final CliArg prevArg = args.put(param, arg);
            if (prevArg != null) {
                throw new ParseException(ParseError.PARAM_ALREADY_BOUND, "Parameter '%s' is already bound a value: '%s'",  param.getIdentifier().getName(), prevArg.getRawValue().get());
            }
            if (!unboundParams.remove(param)) {
                throw new ParseException(ParseError.INTERNAL_ERROR, "Internal Error: Param bound to value wasn't previously unbound: param=%s, value='%s'", param.getIdentifier().getName(), arg.getRawValue());
            }
        }

        public Predicate<CliParam> unboundParamPredicate() {
            return new Predicate<CliParam>() {
                @Override
                public boolean test(CliParam value) {
                    return !args.containsKey(value);
                }
            };
        }
    }

    private static final CliValueTypeMapper<CliParam> PARAM_NAME_MAPPER = new CliValueTypeMapper<>(CliValueType.COMMAND_PARAM_NAME);
    private static final CliValueTypeMapper<CliParam> PARAM_VALUE_MAPPER = new CliValueTypeMapper<>(CliValueType.COMMAND_PARAM_VALUE);
}

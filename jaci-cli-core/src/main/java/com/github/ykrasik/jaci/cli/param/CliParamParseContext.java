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

package com.github.ykrasik.jaci.cli.param;

import com.github.ykrasik.jaci.cli.CliConstants;
import com.github.ykrasik.jaci.cli.assist.AutoComplete;
import com.github.ykrasik.jaci.cli.assist.BoundParams;
import com.github.ykrasik.jaci.cli.assist.CliValueType;
import com.github.ykrasik.jaci.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jaci.cli.exception.ParseError;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.command.CommandArgsImpl;
import com.github.ykrasik.jaci.util.function.Pred;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.trie.Trie;

import java.util.*;

/**
 * A context that lives for the duration of a single parse parameters operation.
 * Contains the state of the parse operation.
 *
 * @author Yevgeny Krasik
 */
public class CliParamParseContext {
    private final List<CliParam> params;
    private final Trie<CliParam> paramsTrie;

    /**
     * The values that were parsed.
     */
    private final Map<CliParam, Object> parsedValues;

    /**
     * Parameters that haven't been parsed yet.
     */
    private final Queue<CliParam> unboundParams;

    /**
     * When this is {@code present}, the previous argument was a call-by-name, so the next argument is expected to be
     * parsed by this parameter.
     */
    private Opt<CliParam> nextNamedParam = Opt.absent();

    public CliParamParseContext(List<CliParam> params, Trie<CliParam> paramsTrie) {
        this.params = Objects.requireNonNull(params, "params");
        this.paramsTrie = Objects.requireNonNull(paramsTrie, "paramsTrie");

        this.parsedValues = new HashMap<>(params.size());
        this.unboundParams = new LinkedList<>(params);
    }

    /**
     * Parse the argument.
     * The parameter that actually parses the argument depends on the context's state:
     *   If the previous argument was a call-by-name argument, the parameter specified by that call-by-name will parse
     *   the argument.
     *   Otherwise, the next unbound positional parameter will parse this argument.
     *
     * @param arg Argument to parse.
     * @throws ParseException If an error occurred while parsing the argument.
     */
    public void parseValue(String arg) throws ParseException {
        if (isParamValue(arg)) {
            if (nextNamedParam.isPresent()) {
                final boolean parsed = parseNextNamedParam(arg);
                nextNamedParam = Opt.absent();
                if (parsed) {
                    // Parse operation completed successfully.
                    return;
                }

                // Parse operation completed with a fallback.
                // 'arg' must then be parsed by the next unbound parameter.
            }
            final CliParam nextUnboundParam = getNextUnboundParam(arg);
            addArg(nextUnboundParam, nextUnboundParam.parse(arg));
        } else {
            // Arg is not a viable param value, it is a call-by-name.
            if (nextNamedParam.isPresent()) {
                // Notify the current next named param that it isn't going to receive a value.
                final CliParam param = nextNamedParam.get();
                addArg(param, param.noValue());
                nextNamedParam = Opt.absent();
            }
            setNextNamedParam(arg);
        }
    }

    private boolean isParamValue(String arg) {
        // Arg can be a param value if:
        //   1. It doesn't start with '-'.
        //   2. It does start with a '-', but the following character is a digit, meaning it is a negative number.
        return !arg.startsWith(CliConstants.NAMED_PARAM_PREFIX) || arg.length() >= 2 && Character.isDigit(arg.charAt(1));
    }

    private boolean parseNextNamedParam(String arg) throws ParseException {
        final CliParam param = nextNamedParam.get();
        try {
            addArg(param, param.parse(arg));

            // Parse operation completed successfully.
            return true;
        } catch (ParseException e) {
            // Try recovering with a fallback by notifying the previous named  param it isn't going to receive a value.
            // This can only succeed with very specific parameters and very specific cases.
            try {
                addArg(param, param.noValue());

                // Parse operation completed with a fallback.
                return false;
            } catch (ParseException ignored) {
                // Throw the original exception if the fallback failed.
                throw e;
            }
        }
    }

    private void setNextNamedParam(String arg) throws ParseException {
        final String paramName = arg.substring(1);
        if (paramName.isEmpty()) {
            throw new ParseException(ParseError.INVALID_PARAM, "No parameter name specified after '"+CliConstants.NAMED_PARAM_PREFIX+"'!");
        }

        nextNamedParam = paramsTrie.get(paramName);
        if (!nextNamedParam.isPresent()) {
            throw new ParseException(ParseError.INVALID_PARAM, "Invalid parameter name: '"+paramName+'\'');
        }
    }

    private CliParam getNextUnboundParam(String arg) throws ParseException {
        final CliParam param = unboundParams.peek();
        if (param == null) {
            throw new ParseException(ParseError.NO_MORE_PARAMS, "Excess argument: '"+arg+'\'');
        }
        return param;
    }

    /**
     * Create {@link CommandArgs} out of this context's already parsed values.
     * In case the last argument parsed by the context was a call-by-name (ended with '-{paramName}'),
     * the context will try to resolve that parameter's value by calling the parameter's {@link CliParam#noValue()}.
     * In case not all parameters were bound to values, the context will try to resolve those unbound parameters by
     * calling their {@link CliParam#unbound()}.
     *
     * @return A {@link CommandArgs} if the context managed to construct one according to the above rules.
     * @throws ParseException If an error occurred, according to the above rules.
     */
    public CommandArgs createCommandArgs() throws ParseException {
        // In case the last arg was a call-by-name , have that parameter parse a 'no-value' value.
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
            final Object parsedValue = parsedValues.get(param);
            if (parsedValue == null && !param.isNullable()) {
                // If there is a missing arg value at this point, this is an internal error.
                throw new IllegalStateException("Internal Error: Not all params have been parsed! Missing=" + param);
            }
            commandArgs.add(parsedValue);
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

    /**
     * Create {@link ParamAssistInfo} out of this context's state (already parsed values, and parameters still needing
     * to be parsed).
     * In case the last argument parsed by the context was a call-by-name (ended with '-{paramName}'),
     * the returned assist info will contain that parameter's auto complete.
     * Otherwise, if the given prefix starts with '-' (call-by-name prefix), the returned assist info will contain
     * suggestions for unbound parameter names.
     * Otherwise the returned assist info will contain suggestions for values for the next unbound positional parameter.
     *
     * @param prefix Prefix to create assistance for.
     * @return A {@link ParamAssistInfo} if the context managed to construct one according to the above rules.
     * @throws ParseException If an error occurred, according to the above rules.
     */
    public ParamAssistInfo createParamAssistInfo(String prefix) throws ParseException {
        if (nextNamedParam.isPresent()) {
            // The last parsed value was a call-by-name (ended with '-{paramName}').
            // Have that named parameter auto-complete the prefix.
            final CliParam param = nextNamedParam.get();
            final AutoComplete autoComplete = param.autoComplete(prefix);
            final BoundParams boundParams = new BoundParams(parsedValues, nextNamedParam);
            return new ParamAssistInfo(boundParams, autoComplete);
        }

        final CliParam nextParam = getNextUnboundParam(prefix);

        // Check if 'prefix' starts with the named parameter call prefix.
        final AutoComplete autoComplete;
        if (prefix.startsWith(CliConstants.NAMED_PARAM_PREFIX)) {
            // Prefix starts with the named parameter call prefix.
            // Auto complete it with possible unbound parameter names.
            // TODO: Can also be a negative number... which cannot be auto-completed.
            final String paramNamePrefix = prefix.substring(1);
            autoComplete = autoCompleteParamName(paramNamePrefix);
        } else {
            // Prefix doesn't start with the named parameter call prefix.
            // Have the next unbound parameter auto complete it's value.
            autoComplete = nextParam.autoComplete(prefix);
        }
        final BoundParams boundParams = new BoundParams(parsedValues, Opt.of(nextParam));
        return new ParamAssistInfo(boundParams, autoComplete);
    }

    private AutoComplete autoCompleteParamName(String prefix) {
        final Trie<CliParam> prefixParams = paramsTrie.subTrie(prefix);
        final Trie<CliParam> unboundPrefixParams = prefixParams.filter(new Pred<CliParam>() {
            @Override
            public boolean test(CliParam value) {
                // Only keep unbound params.
                return !parsedValues.containsKey(value);
            }
        });
        final Trie<CliValueType> paramNamePossibilities = unboundPrefixParams.mapValues(CliValueType.COMMAND_PARAM_NAME.<CliParam>getMapper());
        return new AutoComplete(prefix, paramNamePossibilities);
    }

    private void addArg(CliParam param, Object parsedValue) throws ParseException {
        doAddArg(param, parsedValue);
        if (!unboundParams.remove(param)) {
            throw new IllegalStateException("Internal Error: Param bound to value wasn't previously unbound: param="+param.getIdentifier().getName()+", value='"+parsedValue+'\'');
        }
    }

    private void doAddArg(CliParam param, Object parsedValue) throws ParseException {
        if (parsedValues.containsKey(param)) {
            throw new ParseException(ParseError.PARAM_ALREADY_BOUND, "Parameter '"+param.getIdentifier().getName()+"' is already bound a value: '"+parsedValues.get(param)+'\'');
        }
        parsedValues.put(param, parsedValue);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CliParamParseContext{");
        sb.append("parsedValues=").append(parsedValues);
        sb.append(", unboundParams=").append(unboundParams);
        sb.append('}');
        return sb.toString();
    }
}

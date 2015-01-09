/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.command.parameter;

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.Tries;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteType;
import com.github.ykrasik.jerminal.internal.command.CommandArgsImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A manager for {@link CommandParam}s.<br>
 * Can parse and auto complete positional and named args.<br>
 * Keeps a state of the parsing process that can be queried later.
 *
 * @author Yevgeny Krasik
 */
public class CommandParamManager {
    private static final Function<CommandParam, AutoCompleteType> AUTO_COMPLETE_TYPE_MAPPER = new Function<CommandParam, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(CommandParam input) {
            return input.getType() == ParamType.FLAG ? AutoCompleteType.COMMAND_PARAM_FLAG : AutoCompleteType.COMMAND_PARAM_NAME;
        }
    };

    private final Trie<CommandParam> params;
    private final List<CommandParam> positionalParams;

    private final List<CommandParam> unboundParams;
    private final Map<String, Object> boundParamValues;
    private final Map<String, String> boundParamRawValues;

    private Optional<CommandParam> currentParam;

    public CommandParamManager(Trie<CommandParam> params, List<CommandParam> positionalParams) {
        this.params = Objects.requireNonNull(params);
        this.positionalParams = Objects.requireNonNull(positionalParams);
        this.unboundParams = new ArrayList<>(positionalParams);
        this.boundParamValues = new HashMap<>(positionalParams.size());
        this.boundParamRawValues = new HashMap<>(positionalParams.size());
        this.currentParam = !positionalParams.isEmpty() ? Optional.of(positionalParams.get(0)) : Optional.<CommandParam>absent();
    }

    /**
     * @param args Args to be parsed.
     * @return Parsed args.
     * @throws ParseException If an invalid value was supplied for a param, or if a param wasn't bound.
     */
    public CommandArgs parseCommandArgs(List<String> args) throws ParseException {
        // Parse all params that have been bound.
        parse(args);

        // Bind the remaining unbound params.
        // It is up to the param to decide whether they can be parsed this way.
        for (CommandParam unboundParam : unboundParams) {
            // TODO: Double dispatch somehow?
            final Object value = unboundParam.unbound();
            boundParamValues.put(unboundParam.getName(), value);
        }

        final Queue<Object> positionalArgValues = createPositionalArgValues();
        return new CommandArgsImpl(boundParamValues, positionalArgValues);
    }

    private void parse(List<String> args) throws ParseException {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        for (String rawArg : args) {
            // FIXME: Change the flow here to getNextParamToParse() and parseNextParam()
            final ParsedParam parsedParam = parseParam(rawArg);

            // Mark the param as bound.
            final String paramName = parsedParam.param.getName();
            boundParamValues.put(paramName, parsedParam.value);
            boundParamRawValues.put(paramName, parsedParam.rawValue);
            unboundParams.remove(parsedParam.param);
        }

        if (unboundParams.isEmpty()) {
            // TODO: I hate this.
            currentParam = Optional.absent();
        }
    }

    private ParsedParam parseParam(String rawArg) throws ParseException {
        if (unboundParams.isEmpty()) {
            throw noMoreParams();
        }

        // rawArg is expected to be either:
        // 1. A value that is accepted by the next param in unboundParams.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final int delimiterIndex = rawArg.indexOf(ShellConstants.ARG_VALUE_DELIMITER);
        if (delimiterIndex == -1) {
            // rawArg does not contain a delimiter.
            // It can either be the value of the next positional param or the name of a flag.
            // TODO: This should be replaced with some form of double dispatch.
            final Optional<CommandParam> param = params.get(rawArg);
            if (param.isPresent() && param.get().getType() == ParamType.FLAG) {
                // rawArg is indeed a flag, however:
                // If it is unbound, use it.
                // If it is bound, it could still be a valid value for the next positional param.
                if (!boundParamValues.containsKey(rawArg)) {
                    currentParam = Optional.of(param.get());
                    return new ParsedParam(param.get(), true, "true");
                }
            }

            // Try to parse rawArg as the value of the next positional param.
            final CommandParam nextPositionalParam = unboundParams.get(0);
            currentParam = Optional.of(nextPositionalParam);
            final Object value = nextPositionalParam.parse(rawArg);
            return new ParsedParam(nextPositionalParam, value, rawArg);
        }

        // rawArg contains a '=', the part before the '=' is expected to be a valid, unbound param.
        final String paramName = rawArg.substring(0, delimiterIndex);
        final CommandParam param = parseUnboundParam(paramName);

        final String rawValue = rawArg.substring(delimiterIndex + 1);
        final Object value = param.parse(rawValue);

        return new ParsedParam(param, value, rawValue);
    }

    private CommandParam parseUnboundParam(String paramName) throws ParseException {
        final Optional<CommandParam> param = params.get(paramName);
        if (!param.isPresent()) {
            throw invalidParam(paramName);
        }
        if (boundParamValues.containsKey(paramName)) {
            throw paramAlreadyBound(paramName, boundParamValues.get(paramName));
        }
        return param.get();
    }

    /**
     * @param args Args to be parsed.
     * @return Auto complete suggestions for the last arg. Every other arg except the last is expected
     *         to be a valid param value.
     * @throws ParseException If any of the args except the last one can't be validly parsed.
     */
    public AutoCompleteReturnValue autoCompleteLastArg(List<String> args) throws ParseException {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        // Parse all params that have been bound.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        parse(argsToBeParsed);

        // After parsing all but the last arg, there were no more unbound params left.
        if (unboundParams.isEmpty()) {
            throw noMoreParams();
        }

        // AutoComplete argPrefix.
        // FIXME: AutoCompleting a Flag doesn't work.
        final String argPrefix = args.get(args.size() - 1);

        // argPrefix is expected to be either:
        // 1. A value that is accepted by the next positional param.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final int delimiterIndex = argPrefix.indexOf(ShellConstants.ARG_VALUE_DELIMITER);
        if (delimiterIndex == -1) {
            // FIXME: If only 1 possibility to autoComplete param name, this is the current param.
            currentParam = Optional.of(unboundParams.get(0));
            return autoCompleteParamNameOrValue(argPrefix);
        }

        // argPrefix contains a delimiter, the part before the '=' is expected to be a valid, unbound param.
        final String paramName = argPrefix.substring(0, delimiterIndex);
        final CommandParam param = parseUnboundParam(paramName);
        currentParam = Optional.of(param);

        final String valuePrefix = argPrefix.substring(delimiterIndex + 1);
        return param.autoComplete(valuePrefix);
    }

    private AutoCompleteReturnValue autoCompleteParamNameOrValue(String prefix) throws ParseException {
        // prefix does not contain a delimiter.
        // It can either be the value of the next positional param or the name of any unbound param.
        // However, there are 2 things that we don't want to do:
        // 1. Offer to autoComplete the name of the last unbound param (just go straight to it's value).
        // 2. Mask autoComplete errors in the absence of other autoComplete possibilities.

        // Try to autoComplete prefix as the name of any unbound param.
        // TODO: Do offer to autoComplete the name of the last unbound parameter, but only if no other choice.
        final Trie<AutoCompleteType> paramNamePossibilities = autoCompleteParamName(prefix);

        // AutoCompleting the param value could throw an exception, which we don't want to mask
        // unless we have other autoComplete possibilities available.
        try {
            final CommandParam nextPositionalParam = unboundParams.get(0);
            final AutoCompleteReturnValue returnValue = nextPositionalParam.autoComplete(prefix);
            if (paramNamePossibilities.isEmpty()) {
                return returnValue;
            } else {
                final Trie<AutoCompleteType> valuePossibilities = returnValue.getPossibilities();
                final Trie<AutoCompleteType> possibilities = valuePossibilities.union(paramNamePossibilities);
                return new AutoCompleteReturnValue(prefix, possibilities);
            }
        } catch (ParseException e) {
            if (!paramNamePossibilities.isEmpty()) {
                return new AutoCompleteReturnValue(prefix, paramNamePossibilities);
            } else {
                throw e;
            }
        }
    }

    private Trie<AutoCompleteType> autoCompleteParamName(String prefix) {
        if (unboundParams.size() == 1) {
            // Don't suggest param names if there is only 1 option available.
            return Tries.emptyTrie();
        }

        final Trie<CommandParam> prefixParams = params.subTrie(prefix);
        final Trie<CommandParam> filteredParams = prefixParams.filter(new Predicate<CommandParam>() {
            @Override
            public boolean apply(CommandParam value) {
                // Filter all bound params.
                return boundParamValues.containsKey(value.getName());
            }
        });
        return filteredParams.map(AUTO_COMPLETE_TYPE_MAPPER);
    }

    private Queue<Object> createPositionalArgValues() throws ParseException {
        final Queue<Object> values = new ArrayDeque<>(positionalParams.size());
        for (CommandParam param : positionalParams) {
            final Object value = boundParamValues.get(param.getName());
            if (value == null) {
                throw new ShellException("Internal error: Param was bound but is missing a value: %s", param.getName());
            }
            values.add(value);
        }
        return values;
    }

    /**
     * @param name Param name to be queried.
     * @return The raw value bound to the param 'name'.
     */
    public Optional<String> getParamRawValue(String name) {
        return Optional.fromNullable(boundParamRawValues.get(name));
    }

    /**
     * @return The current param being parsed or auto completed. If there was an exception during parsing,
     *         this param would be the cause.
     */
    public Optional<CommandParam> getCurrentParam() {
        return currentParam;
    }

    /**
     * @return true if there are params still unbound.
     */
    public boolean hasUnboundParams() {
        return !unboundParams.isEmpty();
    }

    private ParseException noMoreParams() {
        return new ParseException(
            ParseError.NO_MORE_PARAMS,
            "Command does not accept any more parameters."
        );
    }

    private ParseException invalidParam(String paramName) {
        return new ParseException(
            ParseError.INVALID_PARAM,
            "Invalid parameter: '%s'", paramName
        );
    }

    private ParseException paramAlreadyBound(String paramName, Object value) {
        return new ParseException(
            ParseError.PARAM_ALREADY_BOUND,
            "Parameter '%s' is already bound to a value: '%s'", paramName, value
        );
    }

    private static class ParsedParam {
        public final CommandParam param;
        public final Object value;
        public final String rawValue;

        private ParsedParam(CommandParam param, Object value, String rawValue) {
            this.param = checkNotNull(param, "param");
            this.value = checkNotNull(value, "value");
            this.rawValue = checkNotNull(rawValue, "rawValue");
        }
    }
}

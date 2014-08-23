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
import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.ParamAndValue;
import com.github.ykrasik.jerminal.api.command.Command;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.returnvalue.AssistReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains the current state of parsing a command's {@link CommandParam parameters}.<br>
 * Can create a {@link CommandInfo} from the current state with {@link #createCommandInfo()}.
 *
 * @author Yevgeny Krasik
 */
public class CommandParamContext {
    private static final Function<CommandParam, AutoCompleteType> AUTO_COMPLETE_TYPE_MAPPER = new Function<CommandParam, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(CommandParam input) {
            return input.getType() == ParamType.FLAG ? AutoCompleteType.COMMAND_PARAM_FLAG : AutoCompleteType.COMMAND_PARAM_NAME;
        }
    };

    private final Command command;
    private final Trie<CommandParam> params;

    private final List<CommandParam> unboundParams;
    private final Map<String, Object> boundParamValues;
    private final Map<String, String> boundParamRawValues;

    private Optional<CommandParam> currentParam;

    public CommandParamContext(Command command, Trie<CommandParam> params) {
        this.command = command;
        this.params = params;
        final List<CommandParam> commandParams = command.getParams();
        this.unboundParams = new ArrayList<>(commandParams);
        this.boundParamValues = new HashMap<>(commandParams.size());
        this.boundParamRawValues = new HashMap<>(commandParams.size());
        this.currentParam = !commandParams.isEmpty() ? Optional.of(commandParams.get(0)) : Optional.<CommandParam>absent();
    }

    public CommandArgs parseCommandArgs(List<String> args) throws ParseException {
        // Parse all params that have been bound.
        parse(args);

        // Bind the remaining unbound params.
        // It is up to the param to decide whether they can be parsed this way.
        for (CommandParam unboundParam : unboundParams) {
            final Object value = unboundParam.unbound();
            boundParamValues.put(unboundParam.getName(), value);
        }

        return new CommandArgs(boundParamValues);
    }

    private void parse(List<String> args) throws ParseException {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        for (String rawArg : args) {
            final ParsedParam parsedParam = parseParam(rawArg);

            // Mark the param as bound.
            final String paramName = parsedParam.param.getName();
            boundParamValues.put(paramName, parsedParam.value);
            boundParamRawValues.put(paramName, parsedParam.rawValue);
            unboundParams.remove(parsedParam.param);
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
            final Optional<CommandParam> flagOptional = params.get(rawArg);
            if (flagOptional.isPresent() && flagOptional.get().getType() == ParamType.FLAG) {
                // rawArg is indeed a flag, however:
                // If it is unbound, use it.
                // If it is bound, it could still be a valid value for the next positional param.
                if (!boundParamValues.containsKey(rawArg)) {
                    return new ParsedParam(flagOptional.get(), true, "true");
                }
            }

            // Try to parse rawArg as the value of the next positional param.
            final CommandParam nextPositionalParam = unboundParams.get(0);
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
        final Optional<CommandParam> paramOptional = params.get(paramName);
        if (!paramOptional.isPresent()) {
            throw invalidParam(paramName);
        }
        if (boundParamValues.containsKey(paramName)) {
            throw paramAlreadyBound(paramName, boundParamValues.get(paramName));
        }
        return paramOptional.get();
    }

    public AssistReturnValue assistArgs(List<String> args) throws ParseException {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final String rawArg = args.get(args.size() - 1);

        // Parse all params that have been bound.
        parse(argsToBeParsed);

        // AutoComplete rawArg.
        return assistArg(rawArg);
    }

    private AssistReturnValue assistArg(String rawArg) throws ParseException {
        // FIXME: AutoCompleting a Flag doesn't work.
        if (unboundParams.isEmpty()) {
            throw noMoreParams();
        }

        // rawArg is expected to be either:
        // 1. A value that is accepted by the next positional param.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final AutoCompleteReturnValue autoCompleteReturnValue;
        final int delimiterIndex = rawArg.indexOf(ShellConstants.ARG_VALUE_DELIMITER);
        if (delimiterIndex == -1) {
            autoCompleteReturnValue = autoCompleteParamNameOrValue(rawArg);
            // FIXME: If only 1 possibility to autoComplete param name, this is the current param.
            currentParam = Optional.of(unboundParams.get(0));
        } else {
            // rawArg contains a delimiter, the part before the '=' is expected to be a valid, unbound param.
            final String paramName = rawArg.substring(0, delimiterIndex);
            final CommandParam param = parseUnboundParam(paramName);

            final String rawValue = rawArg.substring(delimiterIndex + 1);
            autoCompleteReturnValue = param.autoComplete(rawValue);
            currentParam = Optional.of(param);
        }

        final CommandInfo commandInfo = createCommandInfo();
        return new AssistReturnValue(Optional.of(commandInfo), autoCompleteReturnValue);
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
            return TrieImpl.emptyTrie();
        }

        final Trie<CommandParam> prefixParams = params.subTrie(prefix);
        final Trie<CommandParam> filteredParams = prefixParams.filter(new Predicate<CommandParam>() {
            public boolean apply(CommandParam value) {
                // Filter all bound params.
                return !boundParamValues.containsKey(value.getName());
            }
        });
        return filteredParams.map(AUTO_COMPLETE_TYPE_MAPPER);
    }

    public CommandInfo createCommandInfo() {
        final String name = command.getName();
        final List<ParamAndValue> paramAndValues = createParamAndValues();
        final int currentParamIndex = findCurrentParamIndex();
        return new CommandInfo(name, paramAndValues, currentParamIndex);
    }

    private List<ParamAndValue> createParamAndValues() {
        final List<CommandParam> params = command.getParams();
        final List<ParamAndValue> paramAndValues = new ArrayList<>(params.size());
        for (CommandParam param : params) {
            final Optional<String> value = Optional.fromNullable(boundParamRawValues.get(param.getName()));
            paramAndValues.add(new ParamAndValue(param, value));
        }
        return paramAndValues;
    }

    private int findCurrentParamIndex() {
        if (!currentParam.isPresent() || unboundParams.isEmpty()) {
            return -1;
        }

        final CommandParam currentParam = this.currentParam.get();
        final List<CommandParam> params = command.getParams();
        for (int i = 0; i < params.size(); i++) {
            if (currentParam == params.get(i)) {
                return i;
            }
        }
        throw new ShellException(
            "Internal error: The next unbound parameter does not belong to command!? command=%s, param=%s",
            command, currentParam
        );
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

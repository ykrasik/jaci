package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.autocomplete.CommandParamNameAutoCompleter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.args.ParseBoundParamsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.args.ParseBoundParamsReturnValue.ParseBoundParamsReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue.ParseParamReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:37
 */
public class CommandParamManager {
    private static final char ARG_VALUE_DELIMITER = '=';

    private final List<CommandParam> params;
    private final Map<String, CommandParam> allParamsMap;
    private final List<CommandParam> mandatoryParams;
    private final Trie<CommandParam> paramNamesTrie;
    private final CommandParamNameAutoCompleter autoCompleter;

    public CommandParamManager(List<CommandParam> params) {
        this.params = Collections.unmodifiableList(checkNotNull(params, "params is null!"));
        this.allParamsMap = new HashMap<>(params.size());
        this.mandatoryParams = new ArrayList<>(params.size());
        this.paramNamesTrie = new TrieImpl<>();

        // Analyze params
        for (CommandParam param : params) {
            final String paramName = param.getName();
            if (paramName.indexOf(ARG_VALUE_DELIMITER) != -1) {
                throw new ShellException("Illegal param name: '%s'. Param names cannot contain '%c'!", paramName, ARG_VALUE_DELIMITER);
            }
            if (allParamsMap.containsKey(paramName)) {
                throw new ShellException("Duplicate param detected: '%s'", paramName);
            }

            allParamsMap.put(paramName, param);
            paramNamesTrie.put(paramName, param);
            if (!param.isOptional()) {
                mandatoryParams.add(param);
            }
        }

        this.autoCompleter = new CommandParamNameAutoCompleter(paramNamesTrie);
    }

    public List<CommandParam> getParams() {
        return params;
    }

    public ParseCommandArgsReturnValue parseCommandArgs(List<String> args, ParamParseContext context) {
        // Parse all args that have been passed.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(args, context);
        if (parseBoundParams.isFailure()) {
            return ParseCommandArgsReturnValue.failure(parseBoundParams.getFailure());
        }

        final ParseBoundParamsReturnValueSuccess success = parseBoundParams.getSuccess();
        final Map<String, Object> parsedArgs = success.getParsedArgs();
        final Map<String, CommandParam> unboundParams = success.getUnboundParams();

        // Try to bind all params that haven't been bound yet.
        for (CommandParam unboundParam : unboundParams.values()) {
            // Let the unbound param parse an empty value.
            // It is up to the param to decide if this is legal or not.
            final ParseParamValueReturnValue parseParamValue = unboundParam.parse(Optional.<String>absent(), context);
            if (parseParamValue.isFailure()) {
                return ParseCommandArgsReturnValue.failure(parseParamValue.getFailure());
            }

            final Object value = parseParamValue.getSuccess().getValue();
            parsedArgs.put(unboundParam.getName(), value);
        }

        final CommandArgs commandArgs = new CommandArgs(parsedArgs);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParseBoundParamsReturnValue parseBoundParams(List<String> args, ParamParseContext context) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final Map<String, Object> parsedArgs = new HashMap<>(args.size());
        final Map<String, CommandParam> unboundParams = new HashMap<>(allParamsMap);
        for (String arg : args) {
            final ParseParamReturnValue returnValue = parseParam(arg, context);
            if (returnValue.isFailure()) {
                return ParseBoundParamsReturnValue.failure(returnValue.getFailure());
            }

            final ParseParamReturnValueSuccess success = returnValue.getSuccess();
            final String paramName = success.getParamName();
            final Object value = success.getValue();

            if (parsedArgs.containsKey(paramName)) {
                return ParseBoundParamsReturnValue.failure(
                    ParseReturnValueFailure.paramAlreadyBound(paramName, parsedArgs.get(paramName))
                );
            }

            // Arg has been parsed.
            // Save the parsed value and mark the param as bound.
            parsedArgs.put(paramName, value);
            unboundParams.remove(paramName);
        }

        return ParseBoundParamsReturnValue.success(parsedArgs, unboundParams);
    }

    private ParseParamReturnValue parseParam(String rawArg, ParamParseContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);

        // Extract the param name from rawArg.
        final String paramName = rawArg.substring(0, indexOfDelimiter != -1 ? indexOfDelimiter : rawArg.length());

        // Since the "={value}" part is optional, the rawValue is calculated as follows:
        // 1. If rawArg contained a '=', the rawValue is considered present and equal to whatever came after the '='.
        // 2. If rawArg didn't contain a '=', the rawValue is considered absent.
        final Optional<String> rawValue;
        CommandParam param = allParamsMap.get(paramName);
        if (param == null) {
            // There are special convenience cases, where the param name can be omitted:
            // 1. If the command has only 1 mandatory param and any number of optional params.
            // 2. If the command has only 1 param, either mandatory or optional.
            if (mandatoryParams.size() == 1) {
                // 1 - Assume the paramName is omitted and use rawArg as the value.
                param = mandatoryParams.get(0);
                rawValue = Optional.of(rawArg);
            } else if (allParamsMap.size() == 1) {
                // 2 - Assume the paramName is omitted and use rawArg as the value.
                param = new ArrayList<>(allParamsMap.values()).get(0);
                rawValue = Optional.of(rawArg);
            } else {
                return ParseParamReturnValue.failure(ParseReturnValueFailure.invalidParam(paramName));
            }
        } else {
            // Use the rest of the rawArg as the value, without the '=' (if present).
            rawValue = extractValue(rawArg, indexOfDelimiter);
        }

        final ParseParamValueReturnValue returnValue = param.parse(rawValue, context);
        if (returnValue.isFailure()) {
            return ParseParamReturnValue.failure(returnValue.getFailure());
        }

        final Object parsedValue = returnValue.getSuccess().getValue();
        return ParseParamReturnValue.success(paramName, parsedValue);
    }

    public AutoCompleteReturnValue autoCompleteArgs(List<String> args, ParamParseContext context) {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final String autoCompleteArg = args.get(args.size() - 1);

        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(argsToBeParsed, context);
        if (parseBoundParams.isFailure()) {
            return AutoCompleteReturnValue.parseFailure(parseBoundParams.getFailure());
        }

        final ParseBoundParamsReturnValueSuccess parseBoundParamsSuccess = parseBoundParams.getSuccess();
        final Map<String, CommandParam> unboundParams = parseBoundParamsSuccess.getUnboundParams();
        final Map<String, Object> parsedArgs = parseBoundParamsSuccess.getParsedArgs();
        final AutoCompleteReturnValue returnValue = autoCompleteArg(autoCompleteArg, unboundParams, parsedArgs, context);
        if (returnValue.isFailure()) {
            return returnValue;
        }

        // A successful autoComplete either has 1 or more possibilities.
        // 0 possibilities is considered a failed autoComplete.
        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
        final List<String> possibilities = success.getPossibilities();

        // Having an empty possibilities list here is an internal error.
        if (possibilities.isEmpty()) {
            return AutoCompleteReturnValue.failure(
                AutoCompleteReturnValueFailure.internalError(
                    "Internal error: AutoComplete succeeded, but returned no possibilities!"
                )
            );
        }

        if (possibilities.size() > 1) {
            // More then 1 possibility available, no further processing should be done here.
            return returnValue;
        }

        // There was only 1 way of autoCompleting the arg.
        // Let's try to be as helpful as we can: Let's add a space!
        final String autoCompleteAddition = success.getAutoCompleteAddition();
        return AutoCompleteReturnValue.successSingle(autoCompleteAddition + ' ');
    }

    private AutoCompleteReturnValue autoCompleteArg(String rawArg,
                                                    Map<String, CommandParam> unboundParams,
                                                    Map<String, Object> parsedArgs,
                                                    ParamParseContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);

        // Extract the param name part from rawArg.
        final String rawParamName = rawArg.substring(0, indexOfDelimiter != -1 ? indexOfDelimiter : rawArg.length());

        // We are either autoCompleting a param name or it's value. Determine which.
        if (indexOfDelimiter != -1) {
            // rawArg had a '=', we are autoCompleting the param value.
            // The paramName is expected to be valid and unbound.
            final CommandParam param = allParamsMap.get(rawParamName);
            if (param == null) {
                return AutoCompleteReturnValue.parseFailure(ParseReturnValueFailure.invalidParam(rawParamName));
            }

            final Optional<String> rawValue = extractValue(rawArg, indexOfDelimiter);
            return autoCompleteParamValue(param, rawValue, unboundParams, parsedArgs, context);
        } else {
            // No '=' in the arg, we are autoCompleting the param name.
            // There are special convenience cases, where the param name can be omitted:
            // 1. If the command has only 1 mandatory param and any number of optional params.
            // 2. If the command has only 1 param, either mandatory or optional.
            if (mandatoryParams.size() == 1) {
                // 1 - Assume the paramName is omitted and try to autoComplete the value.
                // FIXME: This is limiting. Should offer autoComplete for both the name and value.
                // FIXME: Use a Trie join.
                final CommandParam param = mandatoryParams.get(0);
                final Optional<String> rawValue = Optional.of(rawArg);
                return autoCompleteParamValue(param, rawValue, unboundParams, parsedArgs, context);
            } else if (allParamsMap.size() == 1) {
                // 2 - Assume the paramName is omitted and try to autoComplete the value.
                // FIXME: This is limiting. Should offer autoComplete for both the name and value.
                // FIXME: Use a Trie join.
                final CommandParam param = new ArrayList<>(allParamsMap.values()).get(0);
                final Optional<String> rawValue = Optional.of(rawArg);
                return autoCompleteParamValue(param, rawValue, unboundParams, parsedArgs, context);
            } else {
                return autoCompleteParamName(rawParamName, unboundParams);
            }
        }
    }

    private AutoCompleteReturnValue autoCompleteParamName(String prefix, Map<String, CommandParam> unboundParams) {
        final AutoCompleteReturnValue returnValue = autoCompleter.autoComplete(prefix, new BoundParamsFilter(unboundParams));
        if (returnValue.isFailure()) {
            return returnValue;
        }

        // Let's be helpful - if there's only 1 possible way of autoCompleting the paramName, add a '=' after it.
        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
        final List<String> possibleParamNames = success.getPossibilities();
        if (possibleParamNames.size() == 1) {
            final String autoCompleteAddition = success.getAutoCompleteAddition();
            return AutoCompleteReturnValue.successSingle(autoCompleteAddition + ARG_VALUE_DELIMITER);
        } else {
            return returnValue;
        }
    }

    private AutoCompleteReturnValue autoCompleteParamValue(CommandParam param,
                                                           Optional<String> rawValue,
                                                           Map<String, CommandParam> unboundParams,
                                                           Map<String, Object> parsedArgs,
                                                           ParamParseContext context) {
        final String paramName = param.getName();

        // Make sure this param isn't already bound.
        if (!unboundParams.containsKey(paramName)) {
            return AutoCompleteReturnValue.parseFailure(
                ParseReturnValueFailure.paramAlreadyBound(paramName, parsedArgs.get(paramName))
            );
        }

        // AutoComplete param values.
        return param.autoComplete(rawValue, context);
    }

    private Optional<String> extractValue(String arg, int indexOfDelimiter) {
        if (indexOfDelimiter == -1) {
            return Optional.absent();
        }

        final String value = arg.substring(indexOfDelimiter + 1);
        return Optional.of(value);
    }

    /**
     * Filters all bound params.
     */
    private static class BoundParamsFilter implements Predicate<CommandParam> {
        private final Map<String, CommandParam> unboundParams;

        private BoundParamsFilter(Map<String, CommandParam> unboundParams) {
            this.unboundParams = unboundParams;
        }

        @Override
        public boolean apply(CommandParam value) {
            // If unboundParams doesn't contain the paramName, it is bound.
            return unboundParams.containsKey(value.getName());
        }
    }
}

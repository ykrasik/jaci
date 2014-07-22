package com.rawcod.jerminal.manager;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.command.param.ParamParseContext;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamValueReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamValueReturnValue.AutoCompleteParamValueReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValue.ParsePartialCommandArgsReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue.ParseParamReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.paramvalue.ParseParamValueReturnValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:37
 */
public class CommandParamManager {
    private static final char ARG_VALUE_DELIMITER = '=';

    private final Map<String, ShellParam> allParamsMap;
    private final List<ShellParam> mandatoryParams;
    private final Trie<ShellParam> paramNamesTrie;

    public CommandParamManager(List<ShellParam> params) {
        allParamsMap = new HashMap<>(params.size());
        mandatoryParams = new ArrayList<>(params.size());
        paramNamesTrie = new TrieImpl<>();

        // Analyze params
        for (ShellParam param : params) {
            final String paramName = param.getName();
            if (allParamsMap.containsKey(paramName)) {
                final String message = String.format("Duplicate paramNames detected: '%s'", paramName);
                throw new RuntimeException(message);
            }

            allParamsMap.put(paramName, param);
            paramNamesTrie.put(paramName, param);
            if (!param.isOptional()) {
                mandatoryParams.add(param);
            }
        }
    }

    public ParseCommandArgsReturnValue parseArgs(List<String> args, ParamParseContext context) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final ParsePartialCommandArgsReturnValue parsePartialReturnValue = parsePartialArgs(args, context);
        if (parsePartialReturnValue.isFailure()) {
            return ParseCommandArgsReturnValue.failure(parsePartialReturnValue.getFailure());
        }

        final ParsePartialCommandArgsReturnValueSuccess success = parsePartialReturnValue.getSuccess();
        final Map<String, Object> parsedArgs = success.getParsedArgs();
        final Map<String, ShellParam> unboundParams = success.getUnboundParams();

        // Make sure all mandatory params are bound to values
        // and bind optional args to their default values (if unbound).
        for (ShellParam unboundParam : unboundParams.values()) {
            final String paramName = unboundParam.getName();
            if (!unboundParam.isOptional()) {
                return ParseCommandArgsReturnValue.failure(
                    ParseReturnValueFailure.from(
                        ParseError.PARAM_NOT_BOUND,
                        "Parse error: Mandatory param '%s' is not bound to a value!", paramName
                    )
                );
            }

            final Object defaultValue = unboundParam.getDefaultValue();
            parsedArgs.put(paramName, defaultValue);
        }

        final CommandArgs commandArgs = new CommandArgs(parsedArgs);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParsePartialCommandArgsReturnValue parsePartialArgs(List<String> args, ParamParseContext context) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final Map<String, Object> parsedArgs = new HashMap<>(args.size());
        final Map<String, ShellParam> unboundParams = new HashMap<>(allParamsMap);
        for (String arg : args) {
            final ParseParamReturnValue returnValue = parseParam(arg, context);
            if (returnValue.isFailure()) {
                return ParsePartialCommandArgsReturnValue.failure(returnValue.getFailure());
            }

            final ParseParamReturnValueSuccess success = returnValue.getSuccess();
            final String paramName = success.getParamName();
            final Object value = success.getValue();

            if (parsedArgs.containsKey(paramName)) {
                return ParsePartialCommandArgsReturnValue.failure(
                    ParseReturnValueFailure.from(
                        ParseError.PARAM_ALREADY_BOUND,
                        "Parse error: Param '%s' is already bound to a value: %s", paramName, parsedArgs.get(paramName)
                    )
                );
            }

            // Param has been parsed.
            // Save the parsed value and mark it as bound.
            parsedArgs.put(paramName, value);
            unboundParams.remove(paramName);
        }

        return ParsePartialCommandArgsReturnValue.success(parsedArgs, unboundParams);
    }

    private ParseParamReturnValue parseParam(String rawArg, ParamParseContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = getIndexOfDelimiter(rawArg);

        // Extract the param name and value from the rawArg.
        // The value part also includes the '=', because it's up to the param to decide
        // whether it's a legal value (for example, for flags it isn't).
        final String paramName = rawArg.substring(0, indexOfDelimiter);

        final Optional<String> rawValue;
        ShellParam param = allParamsMap.get(paramName);
        if (param == null) {
            // There are special convenience cases, where the param name can be ommitted:
            // 1. If the command has only 1 mandatory param and any number of optional params.
            // 2. If the command has only 1 param, either mandatory or optional.
            if (mandatoryParams.size() == 1) {
                // 1 - Use the paramName as the value.
                param = mandatoryParams.get(0);
                rawValue = fromPossiblyEmptyString(rawArg);
            } else if (allParamsMap.size() == 1) {
                // 2 - Use the paramName as the value.
                param = new ArrayList<>(allParamsMap.values()).get(0);
                rawValue = fromPossiblyEmptyString(rawArg);
            } else {
                return ParseParamReturnValue.failure(
                    ParseReturnValueFailure.from(
                        ParseError.INVALID_PARAM,
                        "Parse error: Invalid param: '%s'", paramName
                    )
                );
            }
        } else {
            // Use the rest of the rawArg as the value.
            final String valuePart = rawArg.substring(indexOfDelimiter);
            rawValue = Optional.fromNullable(Strings.emptyToNull(valuePart));
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

        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final ParsePartialCommandArgsReturnValue parsePartialReturnValue = parsePartialArgs(argsToBeParsed, context);
        if (parsePartialReturnValue.isFailure()) {
            return AutoCompleteReturnValue.parseFailure(parsePartialReturnValue.getFailure());
        }

        final ParsePartialCommandArgsReturnValueSuccess success = parsePartialReturnValue.getSuccess();
        final Map<String, ShellParam> unboundParams = success.getUnboundParams();
        final Map<String, Object> parsedArgs = success.getParsedArgs();

        // We are either autoCompleting a param name, or it's value. Determine which.
        // autoCompleteArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final String autoCompleteArg = args.get(args.size() - 1);
        final int indexOfDelimiter = getIndexOfDelimiter(autoCompleteArg);
        final String paramName = autoCompleteArg.substring(0, indexOfDelimiter);
        if (indexOfDelimiter != autoCompleteArg.length()) {
            // autoCompleteArg had a '=', we are autoCompleting the param value.
            // The paramName is expected to be valid and unbound.
            final ShellParam param = paramNamesTrie.get(paramName);
            if (param == null) {
                return AutoCompleteReturnValue.parseFailure(ParseReturnValueFailure.invalidParam(paramName));
            }

            final String valuePart = autoCompleteArg.substring(indexOfDelimiter);
            final Optional<String> rawValue = fromPossiblyEmptyString(valuePart);
            return autoCompleteParamValue(param, rawValue, unboundParams, parsedArgs, context);
        } else {
            // No '=' in the arg, we are autoCompleting the param name.
            // There are special convenience cases, where the param name can be ommitted:
            // 1. If the command has only 1 mandatory param and any number of optional params.
            // 2. If the command has only 1 param, either mandatory or optional.
            if (mandatoryParams.size() == 1) {
                // 1 - Use the paramName as the value.
                final ShellParam param = mandatoryParams.get(0);
                final Optional<String> rawValue = fromPossiblyEmptyString(autoCompleteArg);
                return autoCompleteParamValue(param, rawValue, unboundParams, parsedArgs, context);
            } else if (allParamsMap.size() == 1) {
                // 2 - Use the paramName as the value.
                final ShellParam param = new ArrayList<>(allParamsMap.values()).get(0);
                final Optional<String> rawValue = fromPossiblyEmptyString(autoCompleteArg);
                return autoCompleteParamValue(param, rawValue, unboundParams, parsedArgs, context);
            } else {
                return autoCompleteParamName(paramName, unboundParams);
            }
        }
    }

    private AutoCompleteReturnValue autoCompleteParamName(String rawParamName,
                                                          Map<String, ShellParam> unboundParams) {
        // Get all param names possible with this prefix, that haven't been bound yet.
        final List<String> possibleParamNames = paramNamesTrie.getWordsByFilter(rawParamName, new BoundParamsTrieFilter(unboundParams));

        // Couldn't match any param names.
        if (possibleParamNames.isEmpty()) {
            return AutoCompleteReturnValue.failure(
                AutoCompleteReturnValueFailure.from(
                    AutoCompleteError.NO_POSSIBLE_VALUES,
                    "AutoComplete error: No unbound param starts with '%s'", rawParamName
                )
            );
        }

        if (possibleParamNames.size() == 1) {
            // Only 1 possible paramName, we can use it.
            // The autoCompleteAddition is the difference between the single possible paramName
            // and the raw paramName. Also add a '=' after.
            final String possibility = possibleParamNames.get(0);
            final String autoCompleteAddition = possibility.substring(rawParamName.length()) + ARG_VALUE_DELIMITER;
            return AutoCompleteReturnValue.successSingle(autoCompleteAddition);
        } else {
            // Multiple possible paramNames.
            // The autoCompleteAddition is the difference between the longest possible prefix
            // and the raw paramName. Don't add a '=' after.
            final String longestPrefix = paramNamesTrie.getLongestPrefix(rawParamName);
            final String autoCompleteAddition = longestPrefix.substring(rawParamName.length());
            return AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibleParamNames);
        }
    }

    private AutoCompleteReturnValue autoCompleteParamValue(ShellParam param,
                                                           Optional<String> rawValue,
                                                           Map<String, ShellParam> unboundParams,
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
        final AutoCompleteParamValueReturnValue returnValue = param.autoComplete(rawValue, context);
        if (returnValue.isFailure()) {
            return AutoCompleteReturnValue.failure(returnValue.getFailure());
        }

        final AutoCompleteParamValueReturnValueSuccess success = returnValue.getSuccess();
        final ShellSuggestion suggestion = success.getSuggestion();
        final List<String> possibilities = suggestion.getPossibilities();
        if (possibilities.isEmpty()) {
            return AutoCompleteReturnValue.failure(
                AutoCompleteReturnValueFailure.internalError(
                    "Internal error: AutoComplete did not fail, but possibilities are empty!"
                )
            );
        }

        if (possibilities.size() == 1) {
            // Only 1 possible value, we can use it.
            // The autoCompleteAddition is the difference between the single possible value
            // and the raw value. Also add a space after.
            final String possibility = possibilities.get(0);
            final String autoCompleteAddition = possibility.substring(possibility.length()) + ' ';
            return AutoCompleteReturnValue.successSingle(autoCompleteAddition);
        } else {
            // Multiple possible values.
            // The autoCompleteAddition is the difference between the longest possible prefix
            // and the raw value. Don't add a space after.
            final String longestPrefix = suggestion.getLongestPrefix();
            final String autoCompleteAddition = longestPrefix.substring(rawValue.or("").length());
            return AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibilities);
        }
    }

    private int getIndexOfDelimiter(String arg) {
        // arg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = arg.indexOf(ARG_VALUE_DELIMITER);
        return indexOfDelimiter != -1 ? indexOfDelimiter : arg.length();
    }

    private Optional<String> fromPossiblyEmptyString(String arg) {
        return Optional.fromNullable(Strings.emptyToNull(arg));
    }

    /**
     * A TrieFilter that filters all bound params.
     */
    private static class BoundParamsTrieFilter implements TrieFilter<ShellParam> {
        private final Map<String, ShellParam> unboundParams;

        private BoundParamsTrieFilter(Map<String, ShellParam> unboundParams) {
            this.unboundParams = unboundParams;
        }

        @Override
        public boolean shouldKeep(ShellParam value) {
            // If unboundParams doesn't contain the paramName, it is bound.
            return unboundParams.containsKey(value.getName());
        }
    }
}

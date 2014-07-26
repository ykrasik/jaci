package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.autocomplete.AutoCompleter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
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

    private final List<CommandParam> allParams;
    private final List<CommandParam> mandatoryParams;
    private final List<OptionalCommandParam> optionalParams;
    private final Trie<CommandParam> paramsTrie;
    private final AutoCompleter<CommandParam> autoCompleter;

    public CommandParamManager(List<CommandParam> params) {
        this.allParams = Collections.unmodifiableList(checkNotNull(params, "params is null!"));
        this.mandatoryParams = new ArrayList<>(params.size());
        this.optionalParams = new ArrayList<>(params.size());
        this.paramsTrie = new TrieImpl<>();

        // Analyze allParams
        for (CommandParam param : params) {
            final String paramName = param.getName();
            if (paramName.indexOf(ARG_VALUE_DELIMITER) != -1) {
                throw new ShellException("Illegal param name: '%s'. Param names cannot contain '%c'!", paramName, ARG_VALUE_DELIMITER);
            }
            if (paramsTrie.contains(paramName)) {
                throw new ShellException("Duplicate param detected: '%s'", paramName);
            }

            paramsTrie.put(paramName, param);

            if (isOptional(param)) {
                // Not the prettiest solution, I admit.
                optionalParams.add((OptionalCommandParam) param);
            } else {
                mandatoryParams.add(param);
            }
        }

        this.autoCompleter = new AutoCompleter<>(paramsTrie);
    }

    public List<CommandParam> getAllParams() {
        return allParams;
    }

    public ParseCommandArgsReturnValue parseCommandArgs(List<String> args, ParamParseContext context) {
        // Parse all args that have been passed.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(args, context);
        if (parseBoundParams.isFailure()) {
            return ParseCommandArgsReturnValue.failure(parseBoundParams.getFailure());
        }

        final ParseBoundParamsReturnValueSuccess success = parseBoundParams.getSuccess();
        final Map<String, Object> parsedArgs = success.getParsedArgs();
        final Collection<CommandParam> unboundMandatoryParams = success.getUnboundMandatoryParams();
        final Collection<OptionalCommandParam> unboundOptionalParams = success.getUnboundOptionalParams();

        // Make sure all mandatory allParams have been bound.
        if (!unboundMandatoryParams.isEmpty()) {
            return ParseCommandArgsReturnValue.failure(ParseErrors.paramsNotBound(unboundMandatoryParams));
        }

        // Bind all optional allParams that haven't been bound yet.
        for (OptionalCommandParam unboundParam : unboundOptionalParams) {
            final Object value = unboundParam.getDefaultValue();
            parsedArgs.put(unboundParam.getName(), value);
        }

        final CommandArgs commandArgs = new CommandArgs(parsedArgs);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParseBoundParamsReturnValue parseBoundParams(List<String> args, ParamParseContext context) {
        // Parse all args that have been passed.
        // Keep track of all allParams that are unbound.
        final Map<String, Object> parsedArgs = new HashMap<>(args.size());
        final Set<CommandParam> unboundMandatoryParams = new HashSet<>(mandatoryParams);
        final Set<OptionalCommandParam> unboundOptionalParams = new HashSet<>(optionalParams);
        for (String rawArg : args) {
            final ParseParamReturnValue returnValue = parseParam(rawArg, context);
            if (returnValue.isFailure()) {
                return ParseBoundParamsReturnValue.failure(returnValue.getFailure());
            }

            final ParseParamReturnValueSuccess success = returnValue.getSuccess();
            final CommandParam param = success.getParam();
            final Object value = success.getValue();

            final String paramName = param.getName();
            if (parsedArgs.containsKey(paramName)) {
                return ParseErrors.paramAlreadyBound(paramName, parsedArgs.get(paramName));
            }

            // Mark the param as bound.
            if (isOptional(param)) {
                unboundOptionalParams.remove((OptionalCommandParam) param);
            } else {
                unboundMandatoryParams.remove(param);
            }

            // Save the parsed arg value.
            parsedArgs.put(paramName, value);
        }

        return ParseBoundParamsReturnValue.success(parsedArgs, unboundMandatoryParams, unboundOptionalParams);
    }

    private ParseParamReturnValue parseParam(String rawArg, ParamParseContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some allParams (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name from rawArg.
        final String paramName = rawArg.substring(0, containsDelimiter ? indexOfDelimiter : rawArg.length());

        // Since the "={value}" part is optional, the rawValue is calculated as follows:
        // 1. If rawArg contained a '=', the rawValue is considered present and equal to whatever came after the '='.
        // 2. If rawArg didn't contain a '=', the rawValue is considered absent.
        final Optional<String> rawValue;
        CommandParam param = paramsTrie.get(paramName);
        if (param == null) {
            // There are special convenience cases, where the param name can be omitted:
            // 1. If the command has only 1 mandatory param and any number of optional allParams.
            // 2. If the command has only 1 param, either mandatory or optional.
            if (!containsDelimiter && mandatoryParams.size() == 1) {
                // 1 - Assume the paramName is omitted and use rawArg as the value.
                param = mandatoryParams.get(0);
                rawValue = Optional.of(rawArg);
            } else if (!containsDelimiter && allParams.size() == 1) {
                // 2 - Assume the paramName is omitted and use rawArg as the value.
                param = allParams.get(0);
                rawValue = Optional.of(rawArg);
            } else {
                return ParseErrors.invalidParam(paramName);
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
        return ParseParamReturnValue.success(param, parsedValue);
    }

    public AutoCompleteReturnValue autoCompleteArgs(List<String> args, ParamParseContext context) {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final String autoCompleteArg = args.get(args.size() - 1);

        // Parse all args that have been passed.
        // Keep track of all allParams that are unbound.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(argsToBeParsed, context);
        if (parseBoundParams.isFailure()) {
            return AutoCompleteErrors.parseError(parseBoundParams.getFailure());
        }

        final ParseBoundParamsReturnValueSuccess parseBoundParamsSuccess = parseBoundParams.getSuccess();
        final Map<String, Object> parsedArgs = parseBoundParamsSuccess.getParsedArgs();
        final AutoCompleteReturnValue returnValue = autoCompleteArg(autoCompleteArg, parsedArgs, context);
        if (returnValue.isFailure()) {
            return returnValue;
        }

        // A successful autoComplete either has 1 or more possibilities.
        // 0 possibilities is considered a failed autoComplete.
        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
        final List<String> possibilities = success.getPossibilities();

        // Having an empty possibilities list here is an internal error.
        if (possibilities.isEmpty()) {
            return AutoCompleteErrors.internalErrorEmptyPossibilities();
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
                                                    Map<String, Object> parsedArgs,
                                                    ParamParseContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some allParams (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name part from rawArg.
        final String rawParamName = rawArg.substring(0, containsDelimiter ? indexOfDelimiter : rawArg.length());

        // We are either autoCompleting a param name or it's value. Determine which.
        final AutoCompleteReturnValue returnValue;
        if (containsDelimiter) {
            // rawArg had a '=', we are autoCompleting the param value.
            // The paramName is expected to be valid and unbound.
            final CommandParam param = paramsTrie.get(rawParamName);
            if (param == null) {
                return AutoCompleteErrors.invalidParam(rawParamName);
            }
            if (parsedArgs.containsKey(rawParamName)) {
                return AutoCompleteErrors.paramAlreadyBound(rawParamName, parsedArgs.get(rawParamName));
            }

            // The param is valid and unbound, autoComplete it's value.
            final Optional<String> rawValue = extractValue(rawArg, indexOfDelimiter);
            returnValue = param.autoComplete(rawValue, context);
        } else {
            // No '=' in the arg, we are autoCompleting the param name.
            // There are special convenience cases, where the param name can be omitted:
            // 1. If the command has only 1 mandatory param and any number of optional allParams.
            // 2. If the command has only 1 param, either mandatory or optional.
            // This process is termed 'autoBinding'.
            // FIXME: This is incorrect. If a command has 1 mandatory param, it will always try to auto complete it.
            if (mandatoryParams.size() == 1) {
                // 1 - Assume the paramName is omitted and try to autoComplete the value.
                // FIXME: This is limiting. Should offer autoComplete for both the name and value.
                // FIXME: Use a Trie join.
                final CommandParam param = mandatoryParams.get(0);
                final Optional<String> rawValue = Optional.of(rawArg);
                returnValue = autoCompleteParamValue(param, rawValue, parsedArgs, context);
            } else if (allParams.size() == 1) {
                // 2 - Assume the paramName is omitted and try to autoComplete the value.
                // FIXME: This is limiting. Should offer autoComplete for both the name and value.
                // FIXME: Use a Trie join.
                final CommandParam param = allParams.get(0);
                final Optional<String> rawValue = Optional.of(rawArg);
                returnValue = autoCompleteParamValue(param, rawValue, parsedArgs, context);
            } else {
                returnValue = autoCompleteParamName(rawParamName, parsedArgs);
            }
        }
        return returnValue;
    }

    private AutoCompleteReturnValue autoCompleteParamNameOrValue(String prefix, Map<String, Object> parsedArgs) {
        final CommandParam deducedParam = tryDeduceParam(parsedArgs);
        final CommandParamNameAutoCompleter autoCompleterToUse;
        if (deducedParam != null) {
            final List<String> possibleValues = deducedParam.getPossibleValues();
            autoCompleterToUse = autoCompleter.union(possibleValues);
        } else {
            autoCompleterToUse = autoCompleter;
        }

        if (mandatoryParams.size() == 1) {
            // 1 - Assume the paramName is omitted and try to autoComplete the value.
            // FIXME: This is limiting. Should offer autoComplete for both the name and value.
            // FIXME: Use a Trie join.
            final CommandParam param = mandatoryParams.get(0);
            final Optional<String> rawValue = Optional.of(rawArg);
            returnValue = autoCompleteParamValue(param, rawValue, parsedArgs, context);
        } else if (allParams.size() == 1) {
            // 2 - Assume the paramName is omitted and try to autoComplete the value.
            // FIXME: This is limiting. Should offer autoComplete for both the name and value.
            // FIXME: Use a Trie join.
            final CommandParam param = allParams.get(0);
            final Optional<String> rawValue = Optional.of(rawArg);
            returnValue = autoCompleteParamValue(param, rawValue, parsedArgs, context);
        } else {
            returnValue = autoCompleteParamName(rawParamName, parsedArgs);
        }
    }

    private CommandParam tryDeduceParam(Map<String, Object> parsedArgs) {
        CommandParam param = tryDeduceParam(mandatoryParams, parsedArgs);
        if (param == null) {
            param = tryDeduceParam(allParams, parsedArgs);
        }
        return param;
    }

    private CommandParam tryDeduceParam(List<CommandParam> paramList, Map<String, Object> parsedArgs) {
        // If the param list has more then 1 params, deducing the param is not possible.
        if (paramList.size() != 1) {
            return null;
        }

        // Make sure param isn't bound yet.
        final CommandParam param = paramList.get(0);
        return parsedArgs.containsKey(param.getName()) ? null : param;
    }

    private AutoCompleteReturnValue autoCompleteParamName(String prefix, Map<String, Object> parsedArgs) {
        final AutoCompleteReturnValue autoCompleteReturnValue = autoCompleter.autoComplete(prefix, new BoundParamsFilter(parsedArgs));
        if (autoCompleteReturnValue.isFailure()) {
            // Give a meaningful error message.
            final AutoCompleteReturnValue failure;
            if (autoCompleteReturnValue.getFailure().getError() == AutoCompleteError.NO_POSSIBLE_VALUES) {
                failure = AutoCompleteErrors.noPossibleValuesForParamNamePrefix(prefix);
            } else {
                failure = autoCompleteReturnValue;
            }
            return failure;
        }

        // Let's be helpful - if there's only 1 possible way of autoCompleting the paramName, add a '=' after it.
        final AutoCompleteReturnValueSuccess success = autoCompleteReturnValue.getSuccess();
        final List<String> possibleParamNames = success.getPossibilities();
        final AutoCompleteReturnValue returnValue;
        if (possibleParamNames.size() == 1) {
            final String autoCompleteAddition = success.getAutoCompleteAddition();
            returnValue = AutoCompleteReturnValue.successSingle(autoCompleteAddition + ARG_VALUE_DELIMITER);
        } else {
            returnValue = autoCompleteReturnValue;
        }
        return returnValue;
    }

    private AutoCompleteReturnValue autoCompleteParamValue(CommandParam param,
                                                           Optional<String> rawValue,
                                                           Map<String, Object> parsedArgs,
                                                           ParamParseContext context) {
        final String paramName = param.getName();

        // Make sure this param isn't already bound.
        if (parsedArgs.containsKey(paramName)) {
            return AutoCompleteErrors.paramAlreadyBound(paramName, parsedArgs.get(paramName));
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

    private boolean isOptional(CommandParam param) {
        // Not the prettiest solution...
        return param instanceof OptionalCommandParam;
    }

    /**
     * Filters all bound allParams.
     */
    private static class BoundParamsFilter implements Predicate<CommandParam> {
        private final Map<String, Object> parsedArgs;

        private BoundParamsFilter(Map<String, Object> parsedArgs) {
            this.parsedArgs = parsedArgs;
        }

        @Override
        public boolean apply(CommandParam value) {
            // If parsedArgs contains the paramName, it is bound.
            return !parsedArgs.containsKey(value.getName());
        }
    }
}

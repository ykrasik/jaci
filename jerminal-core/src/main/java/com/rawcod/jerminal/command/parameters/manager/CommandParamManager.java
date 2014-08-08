package com.rawcod.jerminal.command.parameters.manager;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamType;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
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
    static final char ARG_VALUE_DELIMITER = '=';

    private final List<CommandParam> allParams;
    private final List<CommandParam> mandatoryParams;

    private final Map<String, CommandParam> paramMap;
    private final Trie<CommandParam> paramTrie;

    public CommandParamManager(List<CommandParam> params) {
        this.allParams = Collections.unmodifiableList(checkNotNull(params, "params"));
        this.mandatoryParams = new ArrayList<>(params.size());
        this.paramMap = new HashMap<>(params.size());
        this.paramTrie = new TrieImpl<>();

        // Analyze params
        for (CommandParam param : params) {
            final String paramName = param.getName();
            if (paramName.indexOf(ARG_VALUE_DELIMITER) != -1) {
                throw new ShellException("Illegal param name: '%s'. Param names cannot contain '%c'!", paramName, ARG_VALUE_DELIMITER);
            }
            if (paramMap.containsKey(paramName)) {
                throw new ShellException("Duplicate param detected: '%s'", paramName);
            }

            paramMap.put(paramName, param);
            paramTrie.put(paramName + '=', param);

            if (param.getType() == ParamType.MANDATORY) {
                mandatoryParams.add(param);
            }
        }
    }

    public List<CommandParam> getAllParams() {
        return allParams;
    }

    public ParseCommandArgsReturnValue parseCommandArgs(List<String> args, ParseParamContext context) {
        // Parse all args that have been passed.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(args, context);
        if (parseBoundParams.isFailure()) {
            return ParseCommandArgsReturnValue.failure(parseBoundParams.getFailure());
        }

        final ParseBoundParamsReturnValueSuccess success = parseBoundParams.getSuccess();
        final Map<String, Object> parsedArgs = success.getParsedArgs();
        final Collection<CommandParam> unboundParams = success.getUnboundParams();

        // Bind the remaining unbound params.
        // Do this by having them parse an empty value.
        // It is up to the param to decide whether this is legal.
        for (CommandParam unboundParam : unboundParams) {
            final Object value;
            if (unboundParam.getType() == ParamType.FLAG) {
                // If a flag is unbound, it defaults to false.
                value = false;
            } else {
                final ParseParamValueReturnValue returnValue = unboundParam.parse(Optional.<String>absent(), context);
                if (returnValue.isFailure()) {
                    return ParseCommandArgsReturnValue.failure(returnValue.getFailure());
                }

                // Param has been bound.
                value = returnValue.getSuccess().getValue();
            }
            parsedArgs.put(unboundParam.getName(), value);
        }

        final CommandArgs commandArgs = new CommandArgs(parsedArgs);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParseBoundParamsReturnValue parseBoundParams(List<String> args, ParseParamContext context) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final Map<String, Object> parsedArgs = new HashMap<>(args.size());
        final Set<CommandParam> unboundParams = new HashSet<>(allParams);
        for (String rawArg : args) {
            final ParseParamReturnValue returnValue = parseParam(rawArg, parsedArgs, context);
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
            unboundParams.remove(param);

            // Save the parsed arg value.
            parsedArgs.put(paramName, value);
        }

        return ParseBoundParamsReturnValue.success(parsedArgs, unboundParams);
    }

    private ParseParamReturnValue parseParam(String rawArg,
                                             Map<String, Object> parsedArgs,
                                             ParseParamContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name from rawArg.
        final String paramName = rawArg.substring(0, containsDelimiter ? indexOfDelimiter : rawArg.length());

        // Since the "={value}" part is optional, the rawValue is calculated as follows:
        // 1. If rawArg contained a '=', the rawValue is considered present and equal to whatever came after the '='.
        // 2. If rawArg didn't contain a '=', the rawValue is considered absent.
        Optional<String> rawValue = Optional.absent();
        CommandParam param = paramMap.get(paramName);
        if (param == null) {
            // There are special convenience cases, where the param name can be omitted.
            if (!containsDelimiter) {
                param = tryDeduceParam(parsedArgs);
                if (param != null) {
                    rawValue = Optional.of(rawArg);
                }
            }
            if (param == null) {
                // Failed deducing the param.
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

    public AutoCompleteReturnValue autoCompleteArgs(List<String> args, ParseParamContext context) {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final String autoCompleteArg = args.get(args.size() - 1);

        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(argsToBeParsed, context);
        if (parseBoundParams.isFailure()) {
            return AutoCompleteErrors.parseError(parseBoundParams.getFailure());
        }

        final Map<String, Object> parsedArgs = parseBoundParams.getSuccess().getParsedArgs();
        return autoCompleteArg(autoCompleteArg, parsedArgs, context);

        // FIXME: Figure this out.
//        final AutoCompleteReturnValue returnValue = autoCompleteArg(autoCompleteArg, parsedArgs, context);
//        if (returnValue.isFailure()) {
//            return returnValue;
//        }
//
//
//        // A successful autoComplete either has 1 or more possibilities.
//        // 0 possibilities is considered a failed autoComplete.
//        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
//        final List<String> possibilities = success.getSuggestions();
//
//        // Having an empty possibilities list here is an internal error.
//        if (possibilities.isEmpty()) {
//            return AutoCompleteErrors.internalErrorEmptyPossibilities();
//        }
//
//        if (possibilities.size() > 1) {
//            // More then 1 possibility available, no further processing should be done here.
//            return returnValue;
//        }
//
//        // There was only 1 way of autoCompleting the arg.
//        // Let's try to be as helpful as we can: Let's add a space!
//        // FIXME: This isn't correct. We need to either add a space, or a '=', depending on what we autoCompleted.
//        final String autoCompleteAddition = success.getAutoCompleteAddition();
//        return AutoCompleteReturnValue.successSingle(autoCompleteAddition + ' ');
    }

    private AutoCompleteReturnValue autoCompleteArg(String rawArg,
                                                    Map<String, Object> parsedArgs,
                                                    ParseParamContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name part from rawArg.
        final String rawParamName = rawArg.substring(0, containsDelimiter ? indexOfDelimiter : rawArg.length());

        // We are either autoCompleting a param name or it's value. Determine which.
        final AutoCompleteReturnValue returnValue;
        if (containsDelimiter) {
            // rawArg had a '=', we are autoCompleting the param value.
            // The paramName is expected to be valid and unbound.
            final CommandParam param = paramMap.get(rawParamName);
            if (param == null) {
                return AutoCompleteErrors.invalidParam(rawParamName);
            }
            if (parsedArgs.containsKey(rawParamName)) {
                return AutoCompleteErrors.paramAlreadyBound(rawParamName, parsedArgs.get(rawParamName));
            }

            // The param is valid and unbound, autoComplete it's value.
            final Optional<String> rawValue = extractValue(rawArg, indexOfDelimiter);
            return param.autoComplete(rawValue, context);
            // FIXME: Figure this out
//            final AutoCompleteReturnValue autoCompleteReturnValue = param.autoComplete(rawValue, context);
//            if (autoCompleteReturnValue.isFailure()) {
//                return autoCompleteReturnValue;
//            }
//
//            // A successful autoComplete either has 1 or more possibilities.
//            // 0 possibilities is considered a failed autoComplete.
//            final AutoCompleteReturnValueSuccess success = autoCompleteReturnValue.getSuccess();
//            final List<String> possibilities = success.getSuggestions();
//
//            // Having an empty possibilities list here is an internal error.
//            if (possibilities.isEmpty()) {
//                return AutoCompleteErrors.internalErrorEmptyPossibilities();
//            }
//
//            if (possibilities.size() == 1) {
//                // There was only 1 way of autoCompleting the value.
//                // Let's try to be as helpful as we can: Let's add a space!
//                final String autoCompleteAddition = success.getAutoCompleteAddition();
//                returnValue = AutoCompleteReturnValue.successSingle(autoCompleteAddition + ' ');
//            } else {
//                returnValue = autoCompleteReturnValue;
//            }
        } else {
            returnValue = autoCompleteParamName(rawParamName, parsedArgs);

            // TODO: Figure this out.
            // No '=' in the arg, we are autoCompleting the param name.
            // There are special convenience cases, where the param name can be omitted.
//            // FIXME: This is incorrect. If a command has 1 mandatory param, it will always try to auto complete it.
//            if (mandatoryParams.size() == 1) {
//                // 1 - Assume the paramName is omitted and try to autoComplete the value.
//                // FIXME: This is limiting. Should offer autoComplete for both the name and value.
//                // FIXME: Use a Trie join.
//                final CommandParam param = mandatoryParams.get(0);
//                final Optional<String> rawValue = Optional.of(rawArg);
//                returnValue = autoCompleteParamValue(param, rawValue, parsedArgs, context);
//            } else if (allParams.size() == 1) {
//                // 2 - Assume the paramName is omitted and try to autoComplete the value.
//                // FIXME: This is limiting. Should offer autoComplete for both the name and value.
//                // FIXME: Use a Trie join.
//                final CommandParam param = allParams.get(0);
//                final Optional<String> rawValue = Optional.of(rawArg);
//                returnValue = autoCompleteParamValue(param, rawValue, parsedArgs, context);
//            } else {
//                returnValue = autoCompleteParamName(rawParamName, parsedArgs);
//            }
        }
        return returnValue;
    }

//    private AutoCompleteReturnValue autoCompleteParamNameOrValue(String prefix,
//                                                                 Map<String, Object> parsedArgs,
//                                                                 ParamParseContext context) {
//        final CommandParam deducedParam = tryDeduceParam(parsedArgs);
//        AutoCompleter<String> autoCompleterToUse = paramNameAutoCompleter;
//        if (deducedParam != null) {
//            final AutoCompleteReturnValue returnValue = deducedParam.autoComplete(Optional.of(prefix), context);
//            if (returnValue.isSuccess()) {
//                final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
//                final List<String> possibilities = success.getSuggestions();
//                if (possibilities.isEmpty()) {
//                    return AutoCompleteErrors.internalErrorEmptyPossibilities();
//                }
//
//                // Translate the possible ways to autoComplete the deduced param's value into a trie.
//                final Trie<String> paramPossibleValuesTrie = new TrieImpl<>();
//                if (possibilities.size() == 1) {
//                    final String autoCompleteAddition = success.getAutoCompleteAddition();
//                    paramPossibleValuesTrie.put(prefix + autoCompleteAddition, "");
//                } else {
//                    for (String possibility : possibilities) {
//                        paramPossibleValuesTrie.put(possibility, "");
//                    }
//                }
//
//                // Create a trie union from all the param names and the deduced param's possible values.
//                autoCompleterToUse = new AutoCompleter<>(paramNamesTrie.union(paramPossibleValuesTrie));
//            }
//        }
//        return autoCompleterToUse.autoComplete(prefix, Predicates.<String>alwaysTrue());
//    }

    private CommandParam tryDeduceParam(Map<String, Object> parsedArgs) {
        // There are special convenience cases, where the param name can be omitted:
        // 1. If the command has only 1 mandatory param (which is still unbound) and any number of optional params.
        // 2. If the command has only 1 param (which is still unbound), either mandatory or optional.
        CommandParam param = tryDeduceParam(mandatoryParams, parsedArgs);
        if (param == null) {
            param = tryDeduceParam(allParams, parsedArgs);
        }
        return param;
    }

    private CommandParam tryDeduceParam(Collection<CommandParam> params, Map<String, Object> parsedArgs) {
        // If the param list has more then 1 params, deducing the param is not possible.
        if (params.size() != 1) {
            return null;
        }

        // Make sure param isn't bound yet.
        for (CommandParam param : params) {
            // Only has 1 param.
            return parsedArgs.containsKey(param.getName()) ? null : param;
        }
        return null;
    }

    private AutoCompleteReturnValue autoCompleteParamName(String prefix, Map<String, Object> parsedArgs) {
        final Optional<TrieView> paramNamesTrieView = Tries.getTrieViewWithFilter(paramTrie, prefix, new BoundParamsFilter(parsedArgs));
        if (!paramNamesTrieView.isPresent()) {
            return AutoCompleteErrors.noPossibleValuesForParamNamePrefix(prefix);
        }
        return AutoCompleteReturnValue.success(prefix, paramNamesTrieView.get());
        // FIXME: Figure this out
//        final AutoCompleteReturnValue autoCompleteReturnValue = AutoCompleteUtils.autoComplete(prefix, paramNamesTrieView);
//        if (autoCompleteReturnValue.isFailure()) {
//            return autoCompleteReturnValue;
//        }
//
//        // Let's be helpful - if there's only 1 possible way of autoCompleting the paramName, add a '=' after it.
//        final AutoCompleteReturnValueSuccess success = autoCompleteReturnValue.getSuccess();
//        final List<String> possibleParamNames = success.getSuggestions();
//        final AutoCompleteReturnValue returnValue;
//        if (possibleParamNames.size() == 1) {
//            final String autoCompleteAddition = success.getAutoCompleteAddition();
//            returnValue = AutoCompleteReturnValue.successSingle(autoCompleteAddition + ARG_VALUE_DELIMITER);
//        } else {
//            returnValue = autoCompleteReturnValue;
//        }
//        return returnValue;
    }

    private AutoCompleteReturnValue autoCompleteParamValue(CommandParam param,
                                                           Optional<String> rawValue,
                                                           Map<String, Object> parsedArgs,
                                                           ParseParamContext context) {
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

    /**
     * Filters all bound params.
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

package com.rawcod.jerminal.manager;

import com.google.common.base.Splitter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.command.param.ParamParseContext;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.command.param.ShellParamParser;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValueSuccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:37
 */
public class CommandParamManager {
    private static final String ARG_VALUE_DELIMITER = "=";
    private static final Splitter SPLITTER = Splitter.on(ARG_VALUE_DELIMITER);

    private final Map<String, ShellParam> paramMap;
    private final Trie<ShellParam> paramNamesTrie;

    public CommandParamManager(List<ShellParam> params) {
        this.paramMap = Collections.unmodifiableMap(createParamMap(params));
        this.paramNamesTrie = createParamNamesTrie(params);
    }

    private Map<String, ShellParam> createParamMap(List<ShellParam> params) {
        final Map<String, ShellParam> map = new HashMap<>(params.size());
        for (ShellParam param : params) {
            map.put(param.getName(), param);
        }
        return map;
    }

    private Trie<ShellParam> createParamNamesTrie(List<ShellParam> params) {
        final Trie<ShellParam> trie = new TrieImpl<>();
        for (ShellParam param : params) {
            trie.set(param.getName(), param);
        }
        return trie;
    }

    public AutoCompleteReturnValue autoCompleteArgs(List<String> args, ParamParseContext context) {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        final List<String> argsToBeParsed = args.subList(1, args.size());

        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final ParsePartialCommandArgsReturnValue parsePartialReturnValue = parsePartialArgs(argsToBeParsed);
        if (parsePartialReturnValue.isFailure()) {
            return AutoCompleteReturnValue.failureFrom(parsePartialReturnValue.getFailure());
        }

        final ParsePartialCommandArgsReturnValueSuccess success = parsePartialReturnValue.getSuccess();
        final Map<String, ShellParam> unboundParams = success.getUnboundParams();
        final Map<String, Object> parsedArgs = success.getParsedArgs();

        // We are either autoCompleting a param name, or it's value. Determine which.
        final String autoCompleteArg = args.get(args.size() - 1);
        final List<String> splitArg = SPLITTER.splitToList(autoCompleteArg);
        final String paramName = splitArg.get(0);
        if (splitArg.size() == 1) {
            // No '=' in the arg, we are autoCompleting the param name.
            return autoCompleteParamName(paramName, unboundParams);
        }

        if (splitArg.size() == 2) {
            // Single '=' in the arg, we are autoCompleting the value.
            // The paramName is expected to be valid and unbound.
            final String rawParamValue = splitArg.get(1);
            return autoCompleteParamValue(paramName, rawParamValue, unboundParams, parsedArgs, context);
        }

        // More then 1 '=' in the arg, syntax error.
        return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.PARSE_ERROR)
            .setParseError(ParseError.INVALID_PARAM)
            .setMessageFormat("Error parsing arg: '%s'", autoCompleteArg)
            .build();
    }

    public ParseCommandArgsReturnValue parseArgs(List<String> args) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final ParsePartialCommandArgsReturnValue parsePartialReturnValue = parsePartialArgs(args);
        if (parsePartialReturnValue.isFailure()) {
            return ParseCommandArgsReturnValue.failureFrom(parsePartialReturnValue.getFailure());
        }

        final ParsePartialCommandArgsReturnValueSuccess success = parsePartialReturnValue.getSuccess();
        final Map<String, Object> parsedArgs = success.getParsedArgs();
        final Map<String, ShellParam> unboundParams = success.getUnboundParams();

        // Make sure all mandatory params are bound to values
        // and bind optional args to their default values (if unbound).
        for (ShellParam unboundParam : unboundParams.values()) {
            final String paramName = unboundParam.getName();
            if (!unboundParam.isOptional()) {
                return ParseCommandArgsReturnValue.failureBuilder(ParseError.PARAM_NOT_BOUND)
                    .setMessageFormat("Mandatory param '%s' is not bound to a value!", paramName)
                    .build();
            }

            final Object defaultValue = unboundParam.getDefaultValue();
            parsedArgs.put(paramName, defaultValue);
        }

        final CommandArgs commandArgs = new CommandArgs(parsedArgs);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParsePartialCommandArgsReturnValue parsePartialArgs(List<String> args) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final Map<String, Object> parsedArgs = new HashMap<>(args.size());
        final Map<String, ShellParam> unboundParams = new HashMap<>(paramMap);
        for (String arg : args) {
            final ParseParamReturnValue returnValue = parse(arg);
            if (returnValue.isFailure()) {
                return ParsePartialCommandArgsReturnValue.failureFrom(returnValue.getFailure());
            }

            final ParseParamReturnValueSuccess success = returnValue.getSuccess();
            final String paramName = success.getParamName();
            final Object value = success.getValue();

            if (parsedArgs.containsKey(paramName)) {
                return ParsePartialCommandArgsReturnValue.failureBuilder(ParseError.PARAM_ALREADY_BOUND)
                    .setMessageFormat("Param '%s' is already bound to a value: %s", paramName, parsedArgs.get(paramName))
                    .build();
            }

            // Param has been parsed.
            // Save the parsed value and mark it as bound.
            parsedArgs.put(paramName, value);
            unboundParams.remove(paramName);
        }

        return ParsePartialCommandArgsReturnValue.success(parsedArgs, unboundParams);
    }

    private ParseParamReturnValue parse(String arg) {

    }

    private AutoCompleteReturnValue autoCompleteParamName(String rawParamName,
                                                          Map<String, ShellParam> unboundParams) {
        final List<String> possibleParamNames = paramNamesTrie.getWordsByFilter(rawParamName, new BoundParamsTrieFilter(unboundParams));

        // Couldn't match any param names.
        if (possibleParamNames.isEmpty()) {
            return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.NO_POSSIBLE_VALUES)
                .setMessageFormat("No unbound param starts with '%s'", rawParamName)
                .build();
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

    private AutoCompleteReturnValue autoCompleteParamValue(String paramName,
                                                           String rawValue,
                                                           Map<String, ShellParam> unboundParams,
                                                           Map<String, Object> parsedArgs,
                                                           ParamParseContext context) {
        // Make sure paramName is valid.
        final ShellParam param = paramNamesTrie.get(paramName);
        if (param == null) {
            return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.PARSE_ERROR)
                .setParseError(ParseError.INVALID_PARAM)
                .setMessageFormat("Invalid param: '%s'", paramName)
                .build();
        }

        // Make sure this param isn't already bound.
        if (!unboundParams.containsKey(paramName)) {
            return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.PARSE_ERROR)
                .setParseError(ParseError.PARAM_ALREADY_BOUND)
                .setMessageFormat("Can't autoComplete: Param '%s' is already bound to a value: '%s'", paramName, parsedArgs.get(paramName))
                .build();
        }

        // AutoComplete param values.
        final ShellParamParser paramParser = param.getParser();
        final AutoCompleteParamReturnValue returnValue = paramParser.autoComplete(rawValue, context);
        if (returnValue.isFailure()) {
            return AutoCompleteReturnValue.failureFrom(returnValue.getFailure());
        }

        final AutoCompleteParamReturnValueSuccess success = returnValue.getSuccess();
        final ShellSuggestion suggestion = success.getSuggestion();
        final List<String> possibilities = suggestion.getPossibilities();
        if (possibilities.isEmpty()) {
            return AutoCompleteReturnValue.failureBuilder(AutoCompleteError.INTERNAL_ERROR)
                .setMessage("Internal error: AutoComplete did not fail, but possibilities are empty!")
                .build();
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
            final String autoCompleteAddition = longestPrefix.substring(rawValue.length());
            return AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibilities);
        }
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
        public boolean shouldFilter(ShellParam value) {
            // If unboundParams doesn't contain the paramName, it is bound.
            return !unboundParams.containsKey(value.getName());
        }
    }
}

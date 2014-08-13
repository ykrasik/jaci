package com.rawcod.jerminal.command.parameters.manager;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieBuilder;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamType;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
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
    private static final char ARG_VALUE_DELIMITER = '=';

    private final List<CommandParam> orderedParams;
    private final Trie<CommandParam> params;

    public CommandParamManager(List<CommandParam> params) {
        this.orderedParams = Collections.unmodifiableList(checkNotNull(params, "params"));
        this.params = createParamTrie(params);
    }

    private Trie<CommandParam> createParamTrie(List<CommandParam> params) {
        final TrieBuilder<CommandParam> builder = new TrieBuilder<>();
        for (CommandParam param : params) {
            final String paramName = param.getName();
            if (paramName.indexOf(ARG_VALUE_DELIMITER) != -1) {
                throw new ShellException("Illegal param name: '%s'. Param names cannot contain '%c'!", paramName, ARG_VALUE_DELIMITER);
            }

            builder.add(paramName, param);
        }
        return builder.build();
    }

    public List<CommandParam> getParams() {
        return orderedParams;
    }

    public ParseCommandArgsReturnValue parseCommandArgs(List<String> args, ParseParamContext context) {
        // Parse all params that have been bound.
        final ParseBoundParamsReturnValue parseBoundParams = parseBoundParams(args, context);
        if (parseBoundParams.isFailure()) {
            return ParseCommandArgsReturnValue.failure(parseBoundParams.getFailure());
        }

        final ParseBoundParamsReturnValueSuccess success = parseBoundParams.getSuccess();
        final Map<String, Object> boundParams = success.getBoundParams();
        final Collection<CommandParam> unboundParams = success.getUnboundParams();

        // Bind the remaining unbound params.
        // Do this by having them parse an empty value.
        // It is up to the param to decide whether this is legal.
        for (CommandParam unboundParam : unboundParams) {
            final ParseParamValueReturnValue returnValue = unboundParam.unbound(context);
            if (returnValue.isFailure()) {
                return ParseCommandArgsReturnValue.failure(returnValue.getFailure());
            }

            // Param has been bound.
            final Object value = returnValue.getSuccess().getValue();
            boundParams.put(unboundParam.getName(), value);
        }

        final CommandArgs commandArgs = new CommandArgs(boundParams);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParseBoundParamsReturnValue parseBoundParams(List<String> args, ParseParamContext context) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final List<CommandParam> unboundParams = new ArrayList<>(orderedParams);
        final Map<String, Object> boundParams = new HashMap<>(args.size());
        for (String rawArg : args) {
            final ParseParamReturnValue returnValue = parseParam(rawArg, unboundParams, boundParams, context);
            if (returnValue.isFailure()) {
                return ParseBoundParamsReturnValue.failure(returnValue.getFailure());
            }

            final ParseParamReturnValueSuccess success = returnValue.getSuccess();
            final CommandParam param = success.getParam();
            final Object value = success.getValue();

            final String paramName = param.getName();
            if (boundParams.containsKey(paramName)) {
                return ParseErrors.paramAlreadyBound(paramName, boundParams.get(paramName));
            }

            // Mark the param as bound.
            boundParams.put(paramName, value);
            unboundParams.remove(param);
        }

        return ParseBoundParamsReturnValue.success(boundParams, unboundParams);
    }

    private ParseParamReturnValue parseParam(String rawArg,
                                             List<CommandParam> unboundParams,
                                             Map<String, Object> boundParams,
                                             ParseParamContext context) {
        if (unboundParams.isEmpty()) {
            return ParseErrors.noMoreParams();
        }

        // rawArg is expected to be either:
        // 1. A value that is accepted by the next param in unboundParams.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name from rawArg.
        final CommandParam param;
        final String rawValue;
        if (containsDelimiter) {
            // rawArg contains a delimiter, the part before the '=' is expected to be a valid, unbound param.
            final String paramName = rawArg.substring(0, indexOfDelimiter);
            final Optional<CommandParam> paramOptional = params.get(paramName);
            if (!paramOptional.isPresent()) {
                return ParseErrors.invalidParam(paramName);
            }
            if (boundParams.containsKey(paramName)) {
                return ParseParamReturnValue.failure(ParseErrors.paramAlreadyBound(paramName, boundParams.get(paramName)).getFailure());
            }
            param = paramOptional.get();
            rawValue = rawArg.substring(indexOfDelimiter + 1);
        } else {
            param = unboundParams.get(0);
            rawValue = rawArg;
        }

        if (!containsDelimiter && param.getType() == ParamType.FLAG) {
            return ParseParamReturnValue.success(param, true);
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

        final ParseBoundParamsReturnValueSuccess success = parseBoundParams.getSuccess();
        final List<CommandParam> unboundParams = success.getUnboundParams();
        final Map<String, Object> boundParams = success.getBoundParams();
        return autoCompleteArg(autoCompleteArg, unboundParams, boundParams, context);
    }

    private AutoCompleteReturnValue autoCompleteArg(String rawArg,
                                                    List<CommandParam> unboundParams,
                                                    Map<String, Object> boundParams,
                                                    ParseParamContext context) {
        if (unboundParams.isEmpty()) {
            return AutoCompleteErrors.parseError(ParseErrors.noMoreParams().getFailure());
        }

        // rawArg is expected to be either:
        // 1. A value that is accepted by the next param in unboundParams.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name from rawArg.
        if (containsDelimiter) {
            // rawArg contains a delimiter, the part before the '=' is expected to be a valid, unbound param.
            final String paramName = rawArg.substring(0, indexOfDelimiter);
            final Optional<CommandParam> paramOptional = params.get(paramName);
            if (!paramOptional.isPresent()) {
                return AutoCompleteErrors.parseError(ParseErrors.invalidParam(paramName).getFailure());
            }
            if (boundParams.containsKey(paramName)) {
                return AutoCompleteErrors.parseError(ParseParamReturnValue.failure(ParseErrors.paramAlreadyBound(paramName, boundParams.get(paramName)).getFailure()).getFailure());
            }

            final CommandParam param = paramOptional.get();
            final String rawValue = rawArg.substring(indexOfDelimiter + 1);
            return param.autoComplete(rawValue, context);
        } else {
            return autoCompleteParamNameOrValue(rawArg, unboundParams, boundParams, context);
        }
    }

    private AutoCompleteReturnValue autoCompleteParamNameOrValue(String prefix,
                                                                 List<CommandParam> unboundParams,
                                                                 Map<String, Object> boundParams,
                                                                 ParseParamContext context) {
        // There are 2 things that we don't want to do:
        // 1. Offer to autoComplete the name of the last unbound param (just go straight to it's value).
        // 2. Mask autoComplete errors in the absence of other autoComplete possibilities.

        final Trie<AutoCompleteType> paramNamePossibilities;
        final CommandParam possibleParam = unboundParams.get(0);
        if (unboundParams.size() == 1 ) {
            paramNamePossibilities = TrieImpl.emptyTrie();
        } else {
            paramNamePossibilities = autoCompleteParamName(prefix, boundParams);
        }

        final AutoCompleteReturnValue autoCompleteValue = possibleParam.autoComplete(prefix, context);
        if (autoCompleteValue.isFailure()) {
            // Failed to autoComplete the paramValue.
            // If we have possible paramName suggestions, use them instead. Otherwise, return the failure.
            if (paramNamePossibilities.isEmpty()) {
                return autoCompleteValue;
            } else {
                return AutoCompleteReturnValue.success(prefix, paramNamePossibilities);
            }
        }

        // Return a union of the possible paramNames and paramValues.
        final Trie<AutoCompleteType> unifiedPossibilities = autoCompleteValue.getSuccess().getPossibilities().union(paramNamePossibilities);
        return AutoCompleteReturnValue.success(prefix, unifiedPossibilities);
    }

    private Trie<AutoCompleteType> autoCompleteParamName(String prefix, Map<String, Object> boundParams) {
        final Trie<CommandParam> prefixParams = params.subTrie(prefix);
        final Trie<CommandParam> filteredParams = prefixParams.filter(new BoundParamsFilter(boundParams));
        return filteredParams.map(AutoCompleteMappers.commandParamNameMapper());
    }

    /**
     * Filters all bound params.
     */
    private static class BoundParamsFilter implements Predicate<CommandParam> {
        private final Map<String, Object> boundParams;

        private BoundParamsFilter(Map<String, Object> boundParams) {
            this.boundParams = boundParams;
        }

        @Override
        public boolean apply(CommandParam value) {
            return !boundParams.containsKey(value.getName());
        }
    }
}

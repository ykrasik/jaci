package com.rawcod.jerminal.command.parameters.manager;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieBuilder;
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
            boundParams.put(unboundParam.getName(), value);
        }

        final CommandArgs commandArgs = new CommandArgs(boundParams);
        return ParseCommandArgsReturnValue.success(commandArgs);
    }

    private ParseBoundParamsReturnValue parseBoundParams(List<String> args, ParseParamContext context) {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final Map<String, Object> boundParams = new HashMap<>(args.size());
        final Set<CommandParam> unboundParams = new HashSet<>(orderedParams);
        for (String rawArg : args) {
            final ParseParamReturnValue returnValue = parseParam(rawArg, boundParams, context);
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
                                             Map<String, Object> boundParams,
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
        final Optional<CommandParam> paramOptional = params.get(paramName);
        if (!paramOptional.isPresent()) {
            return ParseErrors.invalidParam(paramName);
        }
        final CommandParam param = paramOptional.get();

        // Use the rest of the rawArg as the value, without the '=' (if present).
        final Optional<String> rawValue = extractValue(rawArg, indexOfDelimiter);

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

        final Map<String, Object> boundParams = parseBoundParams.getSuccess().getBoundParams();
        return autoCompleteArg(autoCompleteArg, boundParams, context);
    }

    private AutoCompleteReturnValue autoCompleteArg(String rawArg,
                                                    Map<String, Object> boundParams,
                                                    ParseParamContext context) {
        // rawArg is expected to be from the form "{name}={value}".
        // The "={value}" part is optional for some params (for example, flags).
        final int indexOfDelimiter = rawArg.indexOf(ARG_VALUE_DELIMITER);
        final boolean containsDelimiter = indexOfDelimiter != -1;

        // Extract the param name part from rawArg.
        final String rawParamName = rawArg.substring(0, containsDelimiter ? indexOfDelimiter : rawArg.length());

        // We are either autoCompleting a param name or it's value. Determine which.
        if (containsDelimiter) {
            // rawArg had a '=', we are autoCompleting the param value.
            // The paramName is expected to be valid and unbound.
            final Optional<CommandParam> paramOptional = params.get(rawParamName);
            if (!paramOptional.isPresent()) {
                return AutoCompleteErrors.invalidParam(rawParamName);
            }
            if (boundParams.containsKey(rawParamName)) {
                return AutoCompleteErrors.paramAlreadyBound(rawParamName, boundParams.get(rawParamName));
            }

            // The param is valid and unbound, autoComplete it's value.
            final CommandParam param = paramOptional.get();
            final Optional<String> rawValue = extractValue(rawArg, indexOfDelimiter);
            return param.autoComplete(rawValue, context);
        } else {
            return autoCompleteParamName(rawParamName, boundParams);
        }
    }

    private AutoCompleteReturnValue autoCompleteParamName(String prefix, Map<String, Object> boundParams) {
        final Trie<CommandParam> prefixParams = params.subTrie(prefix);
        final Trie<CommandParam> filteredParams = prefixParams.filter(new BoundParamsFilter(boundParams));
        final Trie<AutoCompleteType> paramNamePossibilities = filteredParams.map(AutoCompleteMappers.commandParamNameMapper());
        return AutoCompleteReturnValue.success(prefix, paramNamePossibilities);
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

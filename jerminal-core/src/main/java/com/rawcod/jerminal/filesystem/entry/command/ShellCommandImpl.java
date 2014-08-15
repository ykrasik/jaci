package com.rawcod.jerminal.filesystem.entry.command;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.OutputBuffer;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamType;
import com.rawcod.jerminal.exception.ExecuteException;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 14/08/14
 * Time: 19:24
 */
public class ShellCommandImpl extends AbstractShellEntry implements ShellCommand {
    private static final char ARG_VALUE_DELIMITER = '=';

    private final CommandExecutor executor;
    private final List<CommandParam> positionalParams;
    private final Trie<CommandParam> params;

    public ShellCommandImpl(String name,
                            String description,
                            List<CommandParam> params,
                            CommandExecutor executor) {
        super(name, description);

        this.executor = checkNotNull(executor, "executor");
        this.positionalParams = Collections.unmodifiableList(checkNotNull(params, "params"));
        this.params = createParamTrie(params);
    }

    private Trie<CommandParam> createParamTrie(List<CommandParam> params) {
        final TrieBuilder<CommandParam> builder = new TrieBuilder<>();
        for (CommandParam param : params) {
            final String paramName = param.getName();
            if (!isLegalName(paramName)) {
                throw new ShellException("Illegal param name: '%s'. Param names cannot contain '%c'!", paramName, ARG_VALUE_DELIMITER);
            }

            builder.add(paramName, param);
        }
        return builder.build();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public List<CommandParam> getParams() {
        return Collections.unmodifiableList(positionalParams);
    }

    @Override
    public void execute(CommandArgs args, OutputBuffer output) throws ExecuteException {
        executor.execute(args, output);
    }

    @Override
    public CommandArgs parseCommandArgs(List<String> args) throws ParseException {
        // Parse all params that have been bound.
        final BoundParams returnValue = parseBoundParams(args);
        final List<CommandParam> unboundParams = returnValue.getUnboundParams();
        final Map<String, Object> boundParams = returnValue.getBoundParams();

        // Bind the remaining unbound params.
        // Do this by having them parse an empty value.
        // It is up to the param to decide whether this is legal.
        for (CommandParam unboundParam : unboundParams) {
            final Object value = unboundParam.unbound();
            boundParams.put(unboundParam.getName(), value);
        }

        return new CommandArgs(boundParams);
    }

    private BoundParams parseBoundParams(List<String> args) throws ParseException {
        // Parse all args that have been passed.
        // Keep track of all params that are unbound.
        final List<CommandParam> unboundParams = new ArrayList<>(positionalParams);
        final Map<String, Object> boundParams = new HashMap<>(args.size());

        for (String rawArg : args) {
            final ParsedParam parsedParam = parseParam(rawArg, unboundParams, boundParams);
            final CommandParam param = parsedParam.getParam();
            final Object value = parsedParam.getValue();

            // Mark the param as bound.
            boundParams.put(param.getName(), value);
            unboundParams.remove(param);
        }

        return new BoundParams(unboundParams, boundParams);
    }

    private ParsedParam parseParam(String rawArg,
                                   List<CommandParam> unboundParams,
                                   Map<String, Object> boundParams) throws ParseException {
        if (unboundParams.isEmpty()) {
            throw ParseErrors.noMoreParams();
        }

        // rawArg is expected to be either:
        // 1. A value that is accepted by the next param in unboundParams.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final int delimiterIndex = rawArg.indexOf(ARG_VALUE_DELIMITER);
        if (delimiterIndex == -1) {
            // rawArg does not contain a delimiter.
            // It can either be the value of the next positional param or the name of a flag.
            final Optional<CommandParam> flagOptional = params.get(rawArg);
            if (flagOptional.isPresent() && flagOptional.get().getType() == ParamType.FLAG) {
                // rawArg is indeed a flag, however:
                // If it is unbound, use it.
                // If it is bound, it could still be a valid value for the next positional param.
                if (!boundParams.containsKey(rawArg)) {
                    return new ParsedParam(flagOptional.get(), true);
                }
            }

            // Try to parse rawArg as the value of the next positional param.
            final CommandParam nextPositionalParam = unboundParams.get(0);
            final Object value = nextPositionalParam.parse(rawArg);
            return new ParsedParam(nextPositionalParam, value);
        }

        // rawArg contains a delimiter, the part before the '=' is expected to be a valid, unbound param.
        final String paramName = rawArg.substring(0, delimiterIndex);
        final CommandParam param = parseUnboundParam(paramName, boundParams);

        final String rawValue = rawArg.substring(delimiterIndex + 1);
        final Object value = param.parse(rawValue);

        return new ParsedParam(param, value);
    }

    private CommandParam parseUnboundParam(String paramName, Map<String, Object> boundParams) throws ParseException {
        final Optional<CommandParam> paramOptional = params.get(paramName);
        if (!paramOptional.isPresent()) {
            throw ParseErrors.invalidParam(paramName);
        }
        if (boundParams.containsKey(paramName)) {
            throw ParseErrors.paramAlreadyBound(paramName, boundParams.get(paramName));
        }
        return paramOptional.get();
    }

    @Override
    public AutoCompleteReturnValue autoCompleteArgs(List<String> args) throws ParseException {
        // Only the last arg is up for autoCompletion, the rest are expected to be valid args.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final String rawArg = args.get(args.size() - 1);

        // Parse all params that have been bound.
        final BoundParams returnValue = parseBoundParams(argsToBeParsed);
        final List<CommandParam> unboundParams = returnValue.getUnboundParams();
        final Map<String, Object> boundParams = returnValue.getBoundParams();

        // AutoComplete rawArg.
        return autoCompleteArg(rawArg, unboundParams, boundParams);
    }

    private AutoCompleteReturnValue autoCompleteArg(String rawArg,
                                                    List<CommandParam> unboundParams,
                                                    Map<String, Object> boundParams) throws ParseException {
        if (unboundParams.isEmpty()) {
            throw ParseErrors.noMoreParams();
        }

        // rawArg is expected to be either:
        // 1. A value that is accepted by the next positional param.
        // 2. A tuple of the form "{name}={value}", which can assign a value to any other param.
        final int delimiterIndex = rawArg.indexOf(ARG_VALUE_DELIMITER);
        if (delimiterIndex == -1) {
            return autoCompleteParamNameOrValue(rawArg, unboundParams, boundParams);
        }

        // rawArg contains a delimiter, the part before the '=' is expected to be a valid, unbound param.
        final String paramName = rawArg.substring(0, delimiterIndex);
        final CommandParam param = parseUnboundParam(paramName, boundParams);

        final String rawValue = rawArg.substring(delimiterIndex + 1);
        return param.autoComplete(rawValue);
    }

    private AutoCompleteReturnValue autoCompleteParamNameOrValue(String prefix,
                                                                 List<CommandParam> unboundParams,
                                                                 Map<String, Object> boundParams) throws ParseException {
        // prefix does not contain a delimiter.
        // It can either be the value of the next positional param or the name of any unbound param.
        // However, there are 2 things that we don't want to do:
        // 1. Offer to autoComplete the name of the last unbound param (just go straight to it's value).
        // 2. Mask autoComplete errors in the absence of other autoComplete possibilities.

        // Try to autoComplete prefix as the name of any unbound param.
        final Trie<AutoCompleteType> paramNamePossibilities = autoCompleteParamName(prefix, unboundParams, boundParams);

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

    private Trie<AutoCompleteType> autoCompleteParamName(String prefix,
                                                         List<CommandParam> unboundParams,
                                                         Map<String, Object> boundParams) {
        if (unboundParams.size() == 1 ) {
            // Don't suggest param names if there is only 1 option available.
            return TrieImpl.emptyTrie();
        }

        final Trie<CommandParam> prefixParams = params.subTrie(prefix);
        final Trie<CommandParam> filteredParams = prefixParams.filter(new BoundParamsFilter(boundParams));
        return filteredParams.map(AutoCompleteMappers.commandParamNameMapper());
    }

    public static boolean isLegalName(String name) {
        return name.indexOf(ARG_VALUE_DELIMITER) == -1;
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

package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.manager.CommandParamManager;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.ShellAutoComplete;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.parameters.OptionalShellParam;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

import java.util.*;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public class ShellCommand implements ShellEntry {
    private static final ShellParam[] NO_PARAMS = { };

    private final String name;
    private final String description;
    private final String usage;

    private final List<ShellParam> params;
    private final CommandExecutor executor;

    public ShellCommand(String name,
                        String description,
                        CommandExecutor executor) {
        this(name, description, NO_PARAMS, executor);
    }

    public ShellCommand(String name,
                        String description,
                        ShellParam[] params,
                        CommandExecutor executor) {
        verifyParams(params);

        this.name = name;
        this.description = description;
        this.usage = createUsage(name, params);
        this.params = params;
        this.executor = executor;
    }

    private void verifyParams(ShellParam[] params) {
        // If we encounter an optional param, the rest must be optional as well
        boolean optionalEncountered = false;
        for (ShellParam param : params) {
            final boolean optional = param instanceof OptionalShellParam;
            if (optionalEncountered && !optional) {
                throw new RuntimeException("Once an optional param has been specified, the rest MUST be optional too!");
            }

            if (optional) {
                optionalEncountered = true;
            }
        }
    }

    private String createUsage(String name, ShellParam[] params) {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (ShellParam param : params) {
            sb.append(' ');
            sb.append(param.toString());
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public CommandParamManager getParamManager() {

    }

    public ExecuteReturnValue execute(CommandArgs args) {

    }

    // TODO: Why bother parsing the args? just auto complete the last one?
    public AutoCompleteReturnValue autoComplete(Queue<String> args) {
        // Remove all flags from args
        parseAndRemoveFlags(args, new HashSet<String>(0));

        if (params.length == 0) {
            if (args.isEmpty()) {
                return AutoCompleteReturnValue.success(ShellAutoComplete.none());
            } else {
                final String errorMessage = String.format("Command '%s' takes no arguments.", name);
                return AutoCompleteReturnValue.failureInvalidArgument(errorMessage).withUsage(usage);
            }
        }

        // Parse all args except the last one, which is autoCompleted
        for (ShellParam param : params) {
            if (args.size() <= 1) {
                // AutoComplete the last param.
                final String arg = args.poll();
                final AutoCompleteReturnValue returnValue = param.autoComplete(arg);
                if (!returnValue.isSuccess()) {
                    return returnValue.withUsage(usage);
                }
                return returnValue;
            }

            final String arg = args.poll();
            final ParseReturnValue<?> returnValue = param.parse(arg);
            if (!returnValue.isSuccess()) {
                return AutoCompleteReturnValue.from(returnValue).withUsage(usage);
            }
        }

        if (!args.isEmpty()) {
            if (args.peek().isEmpty()) {
                return AutoCompleteReturnValue.success(ShellAutoComplete.none());
            } else {
                return AutoCompleteReturnValue.failureExcessArgument(args).withUsage(usage);
            }
        }

        // Code isn't supposed to reach this point.
        final String errorMessage = String.format("Internal error autoCompleting arguments for command '%s'", name);
        return AutoCompleteReturnValue.failureInternalError(errorMessage);
    }

    public ParseReturnValue<?> parse(Queue<String> args, Queue<Object> parsedArgs, Set<String> flags) {
        // Parse and remove all flags from args.
        parseAndRemoveFlags(args, flags);

        if (params.length == 0 && !args.isEmpty()) {
            final String errorMessage = String.format("Command '%s' takes no arguments.", name);
            return ParseReturnValue.failureInvalidArgument(errorMessage).withUsage(usage);
        }

        // Parse all args.
        for (ShellParam param : params) {
            final String arg = args.poll();
            final ParseReturnValue<?> returnValue = param.parse(arg);
            if (!returnValue.isSuccess()) {
                return returnValue.withUsage(usage);
            }

            parsedArgs.add(returnValue.getParsedValue());
        }

        if (args.isEmpty()) {
            return ParseReturnValue.success(null);
        } else {
            return ParseReturnValue.failureExcessArgument(args).withUsage(usage);
        }
    }

    public ExecuteReturnValue execute(Queue<Object> parsedArgs, Set<String> flags) {
        final ShellCommandArgs shellCommandArgs = new ShellCommandArgs(parsedArgs);
        return executor.execute(shellCommandArgs, flags);
    }

    private void parseAndRemoveFlags(Queue<String> args, Set<String> flags) {
        final Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            final String arg = iterator.next();
            if (arg.startsWith("-")) {
                // arg is a flag
                flags.add(arg);
                iterator.remove();
            }
        }
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public void install(ShellManager manager) {
        for (ShellParam param : params) {
            param.install(manager);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

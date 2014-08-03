package com.rawcod.jerminal.shell;

import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.parameters.entry.DirectoryParam;
import com.rawcod.jerminal.command.parameters.entry.FileParam;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandImpl;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectoryImpl;
import com.rawcod.jerminal.output.Terminal;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandArgs;
import com.rawcod.jerminal.filesystem.entry.directory.ShellTree;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.shell.parser.ShellCommandParser;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 08/01/14
 */
public class ShellManager {
    private final ShellDirectoryImpl root;
    private final ShellCommandParser shellCommandParser;

    private ShellDirectoryImpl currentDirectory;

    private Terminal terminal;

    public ShellManager() {
        this.root = new ShellDirectoryImpl("/", "root");
        this.shellCommandParser = new ShellCommandParser(this);
        this.currentDirectory = root;

        // TODO: Consider de-hardcoding the names
        addGlobalCommand(createChangeDirectoryShellCommand());
        addGlobalCommand(createListDirectoryShellCommand());
        addGlobalCommand(createDescribeShellCommand());
    }

    private ShellCommandImpl createChangeDirectoryShellCommand() {
        final CommandParam[] params = {
            new DirectoryParam("dir")
        };
        return new ShellCommandImpl("cd", "Change directory", params, new CommandExecutor() {
            @Override
            protected ExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
                final ShellDirectoryImpl directory = args.popDirectory();
                currentDirectory = directory;
                final List<String> path = directory.getPath();
                if ("/".equals(path.get(0))) {
                    // Remove the root '/' from the path.
                    path.remove(0);
                }
                terminal.setCurrentPath(path);
                return ExecuteReturnValue.successNoMessage();
            }
        });
    }

    private ShellCommandImpl createListDirectoryShellCommand() {
        final CommandParam[] params = {
            new OptionalCommandDirectoryParam("dir")
        };
        return new ShellCommandImpl("ls", "List directory", params, new CommandExecutor() {
            @Override
            protected ExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
                final ShellDirectoryImpl directory = args.popDirectory();
                final boolean recursive = flags.contains("-r");
                final ShellTree shellTree = directory.listContent(recursive);
                terminal.displayShellTree(shellTree);
                return ExecuteReturnValue.successNoMessage();
            }
        });
    }

    private ShellCommandImpl createDescribeShellCommand() {
        final CommandParam[] params = {
            new FileParam("command")
        };
        return new ShellCommandImpl("man", "Describe command", params, new CommandExecutor() {
            @Override
            protected ExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
                final ShellCommandImpl command = args.popFile();
                final StringBuilder sb = new StringBuilder();
                sb.append(command.getName());
                sb.append(" : ");
                sb.append(command.getDescription());
                sb.append('\n');
                terminal.displayUsage(command.getUsage());
                return ExecuteReturnValue.success(sb.toString());
            }
        });
    }

    protected void setTerminal(Terminal terminal) {
        // TODO: Not exactly elegant...
        this.terminal = terminal;
    }

    public void addEntries(ShellEntry... entries) {
        for (ShellEntry entry : entries) {
            addEntry(entry);
        }
    }

    public void addEntry(ShellEntry entry) {
        entry.install(this);
        root.addEntry(entry);
    }

    public void addGlobalCommand(ShellCommandImpl command) {
        command.install(this);
        shellCommandParser.addGlobalCommand(command);
    }

    public AutoCompleteReturnValue autoComplete(Queue<String> args) {
        final String commandArg = args.poll();

        if (args.isEmpty()) {
            // The first arg is the only arg on the commandLine, autoComplete command.
            return shellCommandParser.autoComplete(commandArg);
        }

        // The first arg is not the only arg on the commandLine, it is expected to be a valid command.
        final ParseReturnValue<ShellCommandImpl> returnValue = shellCommandParser.parse(commandArg);
        if (!returnValue.isSuccess()) {
            // Couldn't parse the command successfully.
            return AutoCompleteReturnValue.from(returnValue);
        }

        // Let the command autoComplete the args.
        final ShellCommandImpl command = returnValue.getParsedValue();
        return command.autoComplete(args);
    }

    public ParseReturnValue<?> parse(Queue<String> args, Queue<Object> parsedArgs, Set<String> flags) {
        final String commandArg = args.poll();

        final ParseReturnValue<ShellCommandImpl> returnValue = shellCommandParser.parse(commandArg);
        if (!returnValue.isSuccess()) {
            // Couldn't parse the command successfully.
            return returnValue;
        }

        // Add parsed command to parsedArgs.
        final ShellCommandImpl command = returnValue.getParsedValue();
        parsedArgs.add(command);

        // Let the command parse the rest of the args.
        return command.parse(args, parsedArgs, flags);
    }

    public ExecuteReturnValue execute(Queue<Object> parsedArgs, Set<String> flags) {
        if (parsedArgs.isEmpty()) {
            return ExecuteReturnValue.failure("No command provided!");
        }

        // The first arg is the command.
        final ShellCommandImpl command = (ShellCommandImpl) parsedArgs.poll();
        return command.execute(parsedArgs, flags);
    }

    public ShellDirectoryImpl getRoot() {
        return root;
    }

    public ShellDirectoryImpl getCurrentDirectory() {
        return currentDirectory;
    }
}

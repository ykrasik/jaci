/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli;

import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.cli.assist.CommandInfo;
import com.github.ykrasik.jemi.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jemi.cli.assist.Suggestions;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.command.CliCommandOutput;
import com.github.ykrasik.jemi.cli.command.CliCommandOutputImpl;
import com.github.ykrasik.jemi.cli.commandline.CommandLine;
import com.github.ykrasik.jemi.cli.commandline.CommandLineHistory;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jemi.cli.output.CliOutput;
import com.github.ykrasik.jemi.cli.output.CliPrinter;
import com.github.ykrasik.jemi.cli.output.CliSerializer;
import com.github.ykrasik.jemi.command.CommandArgs;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;

import java.util.List;

/**
 * Built on top of a {@link ShellFileSystem}, processes command lines and displays results
 * as a side effect through a {@link com.github.ykrasik.jerminal.old.display.DisplayDriver}.<br>
 * Changes to the underlying {@link ShellFileSystem} will <b>not</b> be picked up after this object is constructed
 * (and why would you want to keep changing the {@link ShellFileSystem} afterwards, anyway?)<br>
 * <br>
 * The Shell does not alter the command line in any way. It is assumed that an external system
 * has ownership of the command line and is the one in charge of manipulating it. The Shell, in turn, simply returns
 * what the new command line should be on each of it's calls.<br>
 * <br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
// TODO: Create AssistInfo and ExecuteReturnValue instead of having a DisplayDriver with side effects?
public class CliShell {
    private final CliCommandHierarchy hierarchy;
    private final CliPrinter printer;
    private final CommandLineHistory history;

    public CliShell(CliCommandHierarchy hierarchy, CliOutput output, CliSerializer serializer, int maxHistory) {
        this(hierarchy, new CliPrinter(output, serializer), new CommandLineHistory(maxHistory));
    }

    /**
     * Package-protected for testing.
     */
    CliShell(@NonNull CliCommandHierarchy hierarchy,
             @NonNull CliPrinter printer,
             @NonNull CommandLineHistory history) {
        this.hierarchy = hierarchy;
        this.printer = printer;
        this.history = history;

        // Welcome message.
        printer.begin();
        printer.println("Welcome to Jemi!");
        printer.println("");
        printer.end();
    }

    /**
     * Provide assistance for the given command line.
     *
     * @param commandLine Command line to provide assistance for.
     * @return {@code true} if the command line was assisted successfully.
     */
    // FIXME: JavaDoc is wrong.
    public Opt<String> assist(String commandLine) {
        printer.begin();
        try {
            return doAssist(commandLine);
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            printer.printException(e);
        } finally {
            printer.end();
        }
        return Opt.absent();
    }

    private Opt<String> doAssist(String rawCommandLine) throws ParseException {
        // This method does a few things:
        // 1. Display command info, if there is any.
        // 2. Determine the suggestions for auto complete.
        // 3. Set the new command line accordingly.
        final CommandLine commandLine = CommandLine.forAssist(rawCommandLine);
        final String pathToCommand = commandLine.getPathToCommand();

        final AutoComplete autoComplete;
        // If we only have 1 arg, we are trying to auto-complete a path to a command.
        if (commandLine.hasCommandArgs()) {
            // The first arg is not the only arg on the commandLine, it is expected to be a valid path to a command,
            // and we are auto-completing the command's parameters.
            final CliCommand command = hierarchy.parsePathToCommand(pathToCommand);

            // Print param assistance info.
            final List<String> args = commandLine.getCommandArgs();
            final ParamAssistInfo assistInfo = command.assist(args);
            final CommandInfo commandInfo = new CommandInfo(command, assistInfo.getBoundParams());
            printer.printCommandInfo(commandInfo);

            autoComplete = assistInfo.getAutoComplete();
        } else {
            // The first arg is the only arg on the commandLine, auto-complete path.
            autoComplete = hierarchy.autoCompletePath(pathToCommand);
        }

        final Opt<Suggestions> suggestions = autoComplete.getSuggestions();
        if (suggestions.isPresent()) {
            printer.printSuggestions(suggestions.get());
        }

        // TODO: Print an error if no suggestions are available?
        return autoComplete.getAutoCompleteSuffix();
    }

    /**
     * Execute the command line.
     *
     * @param commandLine Command line to execute.
     * @return True if the command line was executed successfully.
     */
    public boolean execute(String commandLine) {
        printer.begin();
        try {
            doExecute(commandLine);
            return true;
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            printer.printException(e);
        } finally {
            printer.end();
        }
        return false;
    }

    private void doExecute(String rawCommandLine) throws Exception {
        printer.printCommandLine(rawCommandLine);

        final CommandLine commandLine = CommandLine.forExecute(rawCommandLine);
        if (commandLine.isEmpty()) {
            return;
        }

        // Save command in history.
        history.pushCommandLine(rawCommandLine);

        // Parse command.
        final String pathToCommand = commandLine.getPathToCommand();
        final CliCommand command = hierarchy.parsePathToCommand(pathToCommand);

        // Parse command args.
        final List<String> rawArgs = commandLine.getCommandArgs();
        final CommandArgs args = command.parse(rawArgs);

        // Execute the command.
        final CliCommandOutput commandOutput = new CliCommandOutputImpl(printer);
        command.execute(commandOutput, args);

        if (commandOutput.isPrintDefaultExecutionMessage()) {
            final String message = String.format("Command '%s' executed successfully.", command.getName());
            printer.println(message);
        }
    }

    /**
     * @return Previous command line from history.
     */
    public Opt<String> getPrevCommandLineFromHistory() {
        return history.getPrevCommandLine();
    }

    /**
     * @return Next command line in history.
     */
    public Opt<String> getNextCommandLineFromHistory() {
        return history.getNextCommandLine();
    }

    private void handleParseException(ParseException e) {
        final Opt<CommandInfo> commandInfo = e.getCommandInfo();
        if (commandInfo.isPresent()) {
            printer.printCommandInfo(commandInfo.get());
        }

        final String errorMessage = String.format("Parse Error: %s", e.getMessage());
        printer.errorPrintln(errorMessage);
    }
}

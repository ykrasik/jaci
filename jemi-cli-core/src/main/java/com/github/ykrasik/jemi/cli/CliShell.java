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

import com.github.ykrasik.jemi.cli.assist.*;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.command.CliCommandArgs;
import com.github.ykrasik.jemi.cli.command.CliCommandOutput;
import com.github.ykrasik.jemi.cli.commandline.CommandLine;
import com.github.ykrasik.jemi.cli.commandline.CommandLineHistory;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jemi.cli.output.CliOutput;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jerminal.old.ShellFileSystem;
import com.github.ykrasik.jerminal.old.command.CommandOutputImpl;
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
    private final CliOutput output;
    private final CommandLineHistory history;

    public CliShell(@NonNull CliCommandHierarchy hierarchy, @NonNull CliOutput output, int maxHistory) {
        this.hierarchy = hierarchy;
        this.output = output;
        this.history = new CommandLineHistory(maxHistory);

        // Welcome message.
        output.begin();
        output.println("Welcome to Jemi!");
        output.println("");
        output.end();
    }

    /**
     * Provide assistance for the given command line.
     *
     * @param commandLine Command line to provide assistance for.
     * @return True if the command line was assisted successfully.
     */
    // FIXME: JavaDoc - everything is a side effect.
    public boolean assist(String commandLine) {
        output.begin();
        try {
            doAssist(commandLine);
            return true;
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            output.displayException(e);
        } finally {
            output.end();
        }
        return false;
    }

    private void doAssist(String rawCommandLine) throws ParseException {
        // This method does a few things:
        // 1. Display command info, if there is any.
        // 2. Determine the suggestions for auto complete.
        // 3. Set the new command line accordingly.
        printCommandLine(rawCommandLine);
        final CommandLine commandLine = CommandLine.forAssist(rawCommandLine);

        final AssistInfo assistInfo = getAssistInfo(commandLine);

        final Opt<CommandInfo> commandInfo = assistInfo.getCommandInfo();
        if (commandInfo.isPresent()) {
            output.displayCommandInfo(commandInfo.get());
        }

        final AutoComplete autoComplete = assistInfo.getAutoComplete();

        final Opt<Suggestions> suggestions = autoComplete.getSuggestions();
        if (suggestions.isPresent()) {
            output.displaySuggestions(suggestions.get());
        }

        // TODO: Print an error if no suggestions are available?
        final Opt<String> autoCompletedSuffix = autoComplete.getAutoCompletedSuffix();
        if (autoCompletedSuffix.isPresent()) {
            output.setCommandLine(rawCommandLine + autoCompletedSuffix.get());
        }
    }

    private AssistInfo getAssistInfo(CommandLine commandLine) throws ParseException {
        final String pathToCommand = commandLine.getPathToCommand();

        // If we only have 1 arg, we are trying to auto-complete a path to a command.
        if (!commandLine.hasCommandArgs()) {
            // The first arg is the only arg on the commandLine, auto-complete path.
            final AutoComplete autoComplete = hierarchy.autoCompletePath(pathToCommand);
            return AssistInfo.noCommandInfo(autoComplete);
        }

        // The first arg is not the only arg on the commandLine, it is expected to be a valid path to a command,
        // and we are auto-completing the command's parameters.
        final CliCommand command = hierarchy.parsePathToCommand(pathToCommand);

        // Provide assistance with the command parameters.
        final List<String> args = commandLine.getCommandArgs();
        return command.assist(args);
    }

    /**
     * Execute the command line.
     *
     * @param commandLine Command line to execute.
     * @return True if the command line was executed successfully.
     */
    public boolean execute(String commandLine) {
        output.begin();
        try {
            doExecute(commandLine);
            return true;
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            output.displayException(e);
        } finally {
            output.end();
        }
        return false;
    }

    private void doExecute(String rawCommandLine) throws Exception {
        printCommandLine(rawCommandLine);

        final CommandLine commandLine = CommandLine.forAssist(rawCommandLine);
        if (commandLine.isEmpty()) {
            // Print an empty line.
            output.println("");
            return;
        }

        // Save command in history.
        history.pushCommandLine(rawCommandLine);

        // Parse command.
        final String pathToCommand = commandLine.getPathToCommand();
        final CliCommand command = hierarchy.parsePathToCommand(pathToCommand);

        // Parse command args.
        final List<String> rawArgs = commandLine.getCommandArgs();
        final CliCommandArgs args = command.parse(rawArgs);

        // Execute the command.
        final CliCommandOutput commandOutput = new CommandOutputImpl(output);
        command.execute(commandOutput, args);

        if (!commandOutput.hasInteractions() && !commandOutput.isSuppressDefaultExecutionMessage()) {
            final String message = String.format("Command '%s' executed successfully.", command.getName());
            output.println(message);
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

    private void printCommandLine(String commandLine) {
        output.println("> " + commandLine);
    }

    private void handleParseException(ParseException e) {
        final Opt<CommandInfo> commandInfo = e.getCommandInfo();
        if (commandInfo.isPresent()) {
            output.displayCommandInfo(commandInfo.get());
        }

        final String errorMessage = String.format("Parse Error: %s", e.getMessage());
        output.errorPrintln(errorMessage);
    }
}

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

package com.github.ykrasik.jaci.cli;

import com.github.ykrasik.jaci.cli.assist.AutoComplete;
import com.github.ykrasik.jaci.cli.assist.CommandInfo;
import com.github.ykrasik.jaci.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jaci.cli.assist.Suggestions;
import com.github.ykrasik.jaci.cli.command.CliCommand;
import com.github.ykrasik.jaci.cli.command.CliCommandOutput;
import com.github.ykrasik.jaci.cli.commandline.CommandLine;
import com.github.ykrasik.jaci.cli.commandline.CommandLineHistory;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.cli.gui.CliGui;
import com.github.ykrasik.jaci.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jaci.cli.output.CliOutput;
import com.github.ykrasik.jaci.cli.output.CliPrinter;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.List;
import java.util.Objects;

/**
 * A shell usually refers to the program logic running within a CLI.
 * A CliShell is a component responsible for parsing and executing command lines.
 * The shell has no control over the command line itself, which it receives as a parameter.
 * Which means that any manipulations of the command line are left to the caller.
 * The shell simply returns values that should be appended to the command line.
 *
 * The shell requires a couple of things to be built:
 *   1. A {@link CliCommandHierarchy}: This is the shell's 'file-system'.
 *   2. A {@link CliGui}: A GUI controller, the GUI being everything except the 'terminal screen'.
 *   3. {@link CliPrinter}s for stdOut and stdErr, to which the shell will print.
 *
 * The shell's API methods that print values ({@link #assist(String)}, {@link #execute(String)}) do so
 * as a side effect, by calling the {@link CliPrinter}s the shell was built with.
 *
 * Built through the {@link CliShell.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class CliShell {
    private final CliCommandHierarchy hierarchy;
    private final CliGui gui;
    private final CliPrinter out;
    private final CliPrinter err;
    private final CommandLineHistory history;

    /**
     * Package-protected for testing.
     */
    CliShell(CliCommandHierarchy hierarchy,
             CliGui gui,
             CliPrinter out,
             CliPrinter err,
             CommandLineHistory history) {
        this.hierarchy = hierarchy;
        this.gui = gui;
        this.out = out;
        this.err = err;
        this.history = history;

        // Set initial working directory.
        gui.setWorkingDirectory(hierarchy.getWorkingDirectory());

        // Welcome message.
        out.println("Welcome!");
        out.println("");
    }

    /**
     * @return CLI stdOut.
     */
    public CliPrinter getOut() {
        return out;
    }

    /**
     * @return CLI stdErr.
     */
    public CliPrinter getErr() {
        return err;
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

    /**
     * Provide assistance for the given command line. Assistance means 2 things:
     * <ol>
     *     <li>Auto complete the command line</li>
     *     <li>Print information that is specific to the context of the command line (if the command line indicates
     *         that we are parsing a command, assist with information like values that were already parsed,
     *         next parameter to parse etc').</li>
     *     <li>Print suggestions for the next word to be added to the command line, if any.</li>
     * </ol>
     * Any output is printed as a side effect to the {@link CliOutput} this shell was constructed with.
     * Returns the result of the auto complete operation that should be appended to the command line by the caller.
     *
     * @param commandLine Command line to provide assistance for.
     * @return A value that should be appended to the command line as a result of the auto complete operation.
     */
    public Opt<String> assist(String commandLine) {
        try {
            return doAssist(commandLine);
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            err.printThrowable(e);
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
            out.printCommandInfo(commandInfo);

            autoComplete = assistInfo.getAutoComplete();
        } else {
            // The first arg is the only arg on the commandLine, auto-complete path.
            autoComplete = hierarchy.autoCompletePath(pathToCommand);
        }

        final Opt<Suggestions> suggestions = autoComplete.getSuggestions();
        if (suggestions.isPresent()) {
            out.printSuggestions(suggestions.get());
        }

        // TODO: Print an error if no suggestions are available?
        return autoComplete.getAutoCompleteSuffix();
    }

    /**
     * Execute the command line.
     * The command line will be parsed, verified for correctness and executed if it passes all correctness checks.
     * Any output is printed as a side effect to the {@link CliOutput} this shell was constructed with.
     * If any error occurs while parsing the command line or executing the parsed command line, it will also be printed
     * to the {@link CliOutput} this shell was constructed with.
     *
     * @param commandLine Command line to execute.
     * @return {@code true} if the command line was executed successfully.
     */
    public boolean execute(String commandLine) {
        try {
            doExecute(commandLine);
            return true;
        } catch (ParseException e) {
            handleParseException(e);
        } catch (Exception e) {
            err.printThrowable(e);
        }
        return false;
    }

    private void doExecute(String rawCommandLine) throws Exception {
        out.printCommandLine(hierarchy.getWorkingDirectory(), rawCommandLine);

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
        final CliCommandOutput commandOutput = new CliCommandOutput(gui, out, err);
        command.execute(commandOutput, args);

        if (commandOutput.isPrintDefaultExecutionMessage()) {
            out.println("Command '"+command.getName()+"' executed successfully.");
        }
    }

    private void handleParseException(ParseException e) {
        final Opt<CommandInfo> commandInfo = e.getCommandInfo();
        if (commandInfo.isPresent()) {
            out.printCommandInfo(commandInfo.get());
        }

        err.println("Parse Error: " + e.getMessage());
    }

    /**
     * A builder for a {@link CliShell}.
     */
    // TODO: This is a really useless builder.
    public static class Builder {
        private final CliCommandHierarchy hierarchy;
        private final CliGui gui;
        private final CliPrinter out;
        private final CliPrinter err;
        private int maxCommandHistory = 30;

        public Builder(CliCommandHierarchy hierarchy, CliGui gui, CliPrinter out, CliPrinter err) {
            this.hierarchy = Objects.requireNonNull(hierarchy, "hierarchy");
            this.gui = Objects.requireNonNull(gui, "gui");
            this.out = Objects.requireNonNull(out, "out");
            this.err = Objects.requireNonNull(err, "err");
        }

        /**
         * Set the maximum amount of command history entries to keep.
         *
         * @param maxCommandHistory Max command history entries to keep.
         * @return {@code this}, for chaining.
         */
        public Builder setMaxCommandHistory(int maxCommandHistory) {
            this.maxCommandHistory = maxCommandHistory;
            return this;
        }

        /**
         * @return A {@link CliShell} built out of this builder's parameters.
         */
        public CliShell build() {
            final CommandLineHistory history = new CommandLineHistory(maxCommandHistory);
            return new CliShell(hierarchy, gui, out, err, history);
        }
    }
}

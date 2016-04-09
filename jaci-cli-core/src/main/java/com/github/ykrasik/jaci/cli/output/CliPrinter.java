/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.cli.output;

import com.github.ykrasik.jaci.Identifiable;
import com.github.ykrasik.jaci.IdentifiableComparators;
import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.cli.assist.BoundParams;
import com.github.ykrasik.jaci.cli.assist.CommandInfo;
import com.github.ykrasik.jaci.cli.assist.Suggestions;
import com.github.ykrasik.jaci.cli.command.CliCommand;
import com.github.ykrasik.jaci.cli.directory.CliDirectory;
import com.github.ykrasik.jaci.cli.param.CliParam;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.string.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A component that prints CLI entities to a {@link CliOutput}.
 * Can be overridden to customize the serialization.
 *
 * @author Yevgeny Krasik
 */
public class CliPrinter {
    /** Output to print to. */
    protected final CliOutput output;

    // TODO: Can probably generalise this into a characterMapping thing.
    /** String to use as a tab character. */
    protected final String tab;

    /**
     * Create a printer that will print to the given output with the default '\t' tab string.
     *
     * @param output Output to print to.
     */
    public CliPrinter(CliOutput output) {
        this(output, "\t");
    }

    /**
     * Create a printer that will print to the given output with the given tab string.
     *
     * @param output Output to print to.
     * @param tab String to use as a 'tab' character.
     */
    public CliPrinter(CliOutput output, String tab) {
        this.output = Objects.requireNonNull(output, "output");
        this.tab = Objects.requireNonNull(tab, "tab");
    }

    /**
     * Print a single line.
     *
     * @param text Text to print.
     */
    public void println(String text) {
        output.println(text);
    }

    /**
     * Print the command line.
     *
     * @param workingDirectory Current working directory.
     * @param commandLine Command line to print.
     */
    // TODO: Does this belong here?
    public void printCommandLine(CliDirectory workingDirectory, String commandLine) {
        final String text = '[' + workingDirectory.toPath() + "] " + Objects.requireNonNull(commandLine, "commandLine");
        println(text);
    }

    /**
     * Print a directory and it's content.
     *
     * @param directory Directory to print.
     * @param recursive Whether to recurse into sub-directories.
     */
    public void printDirectory(CliDirectory directory, boolean recursive) {
        printDirectory0(new PrintContext(), directory, recursive);
    }

    private void printDirectory0(PrintContext context, CliDirectory directory, boolean recursive) {
        // Print root directory name.
        printIdentifiable(context, directory);

        // Print child commands.
        final List<CliCommand> commands = new ArrayList<>(directory.getChildCommands());
        Collections.sort(commands, IdentifiableComparators.nameComparator());

        printIdentifiables(context, commands);

        // Print child directories.
        final List<CliDirectory> directories = new ArrayList<>(directory.getChildDirectories());
        Collections.sort(directories, IdentifiableComparators.nameComparator());

        if (!recursive) {
            printIdentifiables(context, directories);
        } else {
            // Recursively print child directories.
            for (CliDirectory childDirectory : directories) {
                context.incIndent();
                printDirectory0(context, childDirectory, true);
                context.decIndent();
            }
        }
    }

    /**
     * Print a command (it's name, description and parameters).
     *
     * @param command Command to describe.
     */
    public void printCommand(CliCommand command) {
        final PrintContext context = new PrintContext();

        // Print command name : description
        printIdentifiable(context, command);

        // Print each param name : description
        printIdentifiables(context, command.getParams());
    }

    /**
     * Print a {@code Throwable}.
     *
     * @param t Throwable to print.
     */
    public void printThrowable(Throwable t) {
        final PrintContext context = new PrintContext();

        Throwable prevThrowable = null;
        Throwable currentThrowable = t;
        while (true) {
            printThrowable0(context, currentThrowable, prevThrowable);
            final Throwable nextThrowable = currentThrowable.getCause();
            if (currentThrowable != nextThrowable && nextThrowable != null) {
                prevThrowable = currentThrowable;
                currentThrowable = nextThrowable;
                context.append("Caused by: ");
            } else {
                break;
            }
        }
    }

    private void printThrowable0(PrintContext context, Throwable currentThrowable, Throwable prevThrowable) {
        context.append(currentThrowable.toString()).println();

        final int framesInCommon;
        final int end;
        final StackTraceElement[] currentStackTrace = currentThrowable.getStackTrace();
        if (prevThrowable != null) {
            final StackTraceElement[] prevStackTrace = prevThrowable.getStackTrace();

            // Compute frames in common.
            int m = currentStackTrace.length - 1;
            int n = prevStackTrace.length - 1;
            while (m >= 0 && n >=0 && currentStackTrace[m].equals(prevStackTrace[n])) {
                m--; n--;
            }
            framesInCommon = currentStackTrace.length - 1 - m;
            end = m;
        } else {
            framesInCommon = 0;
            end = currentStackTrace.length - 1;
        }

        context.incIndent();
        for (int i = 0; i <= end; i++) {
            context.append("at ").append(currentStackTrace[i].toString()).println();
        }
        if (framesInCommon != 0) {
            context.append("... " + framesInCommon).append(" more").println();
        }
        context.decIndent();
    }


    /**
     * Print information about a command. Called to display assistance information about a command, or if a parse
     * error occurred while parsing the command's parameters.
     *
     * @param info Command info to print.
     */
    public void printCommandInfo(CommandInfo info) {
        final PrintContext context = new PrintContext();

        // Print command name : description
        final CliCommand command = info.getCommand();
        printIdentifiable(context, command);

        // Print bound params.
        final BoundParams boundParams = info.getBoundParams();
        printBoundParams(context, command, boundParams);
    }

    private void printBoundParams(PrintContext context, CliCommand command, BoundParams boundParams) {
        final Opt<CliParam> nextParam = boundParams.getNextParam();
        for (CliParam param : command.getParams()) {
            final Opt<Object> value = boundParams.getBoundValue(param);
            final boolean isCurrent = nextParam.isPresent() && nextParam.get() == param;

            // Surround the current param being parsed with -> <-
            if (isCurrent) {
                context.append("-> ").append(tab);
            } else {
                context.append(tab);
            }

            context.append(param.toExternalForm());
            if (value.isPresent()) {
                context.append(" = ").append(value.get().toString());
            }

            // Actually, value.isPresent and isCurrent cannot both be true at the same time.
            if (isCurrent) {
                context.append(tab).append(" <-");
            }

            context.println();
        }
    }

    /**
     * Print suggestions.
     *
     * @param suggestions Suggestions to print.
     */
    public void printSuggestions(Suggestions suggestions) {
        final PrintContext context = new PrintContext();

        context.append("Suggestions:").println();
        context.incIndent();
        printSuggestions0(context, suggestions.getDirectorySuggestions(), "Directories");
        printSuggestions0(context, suggestions.getCommandSuggestions(), "Commands");
        printSuggestions0(context, suggestions.getParamNameSuggestions(), "Parameter names");
        printSuggestions0(context, suggestions.getParamValueSuggestions(), "Parameter values");
        context.decIndent();
    }

    private void printSuggestions0(PrintContext context, List<String> suggestions, String suggestionsTitle) {
        if (!suggestions.isEmpty()) {
            context
                .append(suggestionsTitle)
                .append(": [")
                .append(StringUtils.join(suggestions, ", "))
                .append(']')
                .println();
        }
    }

    private void printIdentifiables(PrintContext context, List<? extends Identifiable> identifiables) {
        context.incIndent();
        for (Identifiable identifiable : identifiables) {
            printIdentifiable(context, identifiable);
        }
        context.decIndent();
    }

    private void printIdentifiable(PrintContext context, Identifiable identifiable) {
        final Identifier identifier = identifiable.getIdentifier();
        final boolean directory = identifiable instanceof CliDirectory;
        if (directory) {
            context.append('[');
        }
        context.append(identifier.getName());
        if (!directory) {
            context.append(" : ").append(identifier.getDescription());
        }
        if (directory) {
            context.append(']');
        }
        context.println();
    }

    /**
     * Assists in printing. Keeps the current indentation level.
     */
    protected class PrintContext {
        /** Current line. */
        private StringBuilder sb = new StringBuilder();

        /** Indent level. */
        private int indent;

        /** Whether indent was appended to the current line. Reset every time a new line is printed. */
        private boolean needIndent = true;

        /**
         * Increase the indent level.
         */
        public void incIndent() {
            indent++;
        }

        /**
         * Decrease the indent level.
         */
        public void decIndent() {
            indent--;
            if (indent < 0) {
                throw new IllegalArgumentException("Invalid indent: " + indent);
            }
        }

        /**
         * Append the string to the current line.
         *
         * @param str String to append.
         * @return {@code this}, for chaining.
         */
        public PrintContext append(String str) {
            indentIfNecessary();
            sb.append(str);
            return this;
        }

        /**
         * Append the character to the current line.
         *
         * @param ch Character to append.
         * @return {@code this}, for chaining.
         */
        public PrintContext append(char ch) {
            indentIfNecessary();
            sb.append(ch);
            return this;
        }

        /**
         * Print the current line to the output.
         *
         * @return {@code this}, for chaining.
         */
        public PrintContext println() {
            CliPrinter.this.println(sb.toString());
            sb = new StringBuilder();
            needIndent = true;
            return this;
        }

        private void indentIfNecessary() {
            if (needIndent) {
                appendIndent();
                needIndent = false;
            }
        }

        private void appendIndent() {
            for (int i = 0; i < indent; i++) {
                sb.append(tab);
            }
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}

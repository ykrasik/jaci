/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 * *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.cli.commandline;

import com.github.ykrasik.jaci.util.string.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a command line.
 *
 * @author Yevgeny Krasik
 */
public class CommandLine {
    /**
     * Elements present in the command line. Each one was typically separated by a whitespace.
     */
    private final List<String> elements;

    private CommandLine(List<String> elements) {
        this.elements = Objects.requireNonNull(elements, "elements");
    }

    /**
     * @return Whether the command line is empty - has no elements.
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * The first element of a command line must be the path to a command.
     *
     * @return The first element of the command line - the path to a command.
     */
    public String getPathToCommand() {
        return elements.get(0);
    }

    /**
     * @return Whether this command line has any elements beyond the first, which is the path to a command.
     */
    public boolean hasCommandArgs() {
        return elements.size() > 1;
    }

    /**
     * The command args start from the 2nd command line element (the first is the path to the command).
     * Should only be called if {@link #hasCommandArgs()} returns {@code true}.
     *
     * @return The command args.
     * @throws IndexOutOfBoundsException If the command line didn't have any args.
     */
    public List<String> getCommandArgs() {
        // The command args start from the 2nd arg.
        return elements.subList(1, elements.size());
    }

    @Override
    public String toString() {
        return StringUtils.join(elements, " ");
    }

    /**
     * Parse the command line for an assist operation.
     * Differs from {@link #forExecute(String)} in that if the command line was empty or ended with a space, a single
     * empty element will be added to it.
     * Essentially, the command line will never return empty from this operation.
     *
     * @param rawCommandLine Command line to parse.
     * @return Parsed command line for assistance.
     */
    public static CommandLine forAssist(String rawCommandLine) {
        final List<String> splitCommandLine = splitCommandLine(rawCommandLine);

        // If the commandLine ends with a space (or is empty), we manually insert an empty arg.
        // This implies that the user wanted assistance about the NEXT argument and not the last one that was typed.
        if (rawCommandLine.isEmpty() || rawCommandLine.endsWith(" ")) {
            splitCommandLine.add("");
        }

        return new CommandLine(splitCommandLine);
    }

    /**
     * Parse the command line for execution. If the given command line is empty, an empty command line will be returned.
     *
     * @param rawCommandLine Command line to parse.
     * @return Parsed command line for execution.
     */
    public static CommandLine forExecute(String rawCommandLine) {
        final List<String> elements = splitCommandLine(rawCommandLine);
        return new CommandLine(elements);
    }

    // FIXME: This pattern doesn't support named parameter calling with strings with whitespace (param="long string")
    // FIXME: Maybe the fix should not be in the pattern. Either way, calling long strings by name doesn't work.

    /**
     * Package-protected for testing
     */
    static List<String> splitCommandLine(String commandLine) {
        // Split the commandLine by whitespace.
        // Allow escaping single and double quoted strings.
        if (Objects.requireNonNull(commandLine, "commandLine").isEmpty()) {
            return new LinkedList<>();
        }

        final List<String> elements = new ArrayList<>();
        final QuoteStack quoteStack = new QuoteStack();
        char c = commandLine.charAt(0);
        boolean matching = !isWhitespace(c);
        int matchStart = 0;
        if (isQuote(c)) {
            quoteStack.push(c);
            matchStart = 1;
        }
        for (int i = 1; i < commandLine.length(); i++) {
            c = commandLine.charAt(i);
            if (matching) {
                if (isWhitespace(c) && quoteStack.isEmpty()) {
                    // Detected a whitespace after matching, and we're not in a quote -
                    // this is a word boundary.
                    elements.add(commandLine.substring(matchStart, i));
                    matching = false;
                } else if (isQuote(c) && quoteStack.quoteMatches(c)) {
                    // Quote closes a previous section of quoted text.
                    elements.add(commandLine.substring(matchStart, i));
                    quoteStack.pop();
                    matching = false;
                }
            } else if (isQuote(c)) {
                // We're not matching - Quote opens a new section of quoted text.
                quoteStack.push(c);
                matchStart = i + 1;
                matching = true;
            } else if (!isWhitespace(c)) {
                // Done slurping whitespaces - non-whitespace detected, start matching.
                matching = true;
                matchStart = i;
            }
        }
        if (matching) {
            // Finished processing commandLine, but we're still matching - add the last word.
            elements.add(commandLine.substring(matchStart, commandLine.length()));
        }

        return elements;
    }

    private static boolean isWhitespace(char c) {
        return c == ' ';
    }

    private static boolean isQuote(char c) {
        return c == '\'' || c == '\"';
    }

    /**
     * Implemented due to lack of support for Deque in GWT.
     */
    private static class QuoteStack {
        private static final int MAX_QUOTE_DEPTH = 2;
        private final Character[] stack = new Character[MAX_QUOTE_DEPTH];
        private int index = -1;

        public boolean isEmpty() {
            return index == -1;
        }

        public boolean quoteMatches(char quote) {
            return !isEmpty() && stack[index] == quote;
        }

        public void push(char c) {
            if (index == MAX_QUOTE_DEPTH - 1) {
                throw new IllegalStateException("Stack is full: quote nesting depth cannot go beyond 2!");
            }
            index++;
            stack[index] = c;
        }

        public void pop() {
            if (index == -1) {
                throw new IllegalStateException("Trying to pop an empty stack!");
            }
            stack[index] = null;
            index--;
        }
    }
}

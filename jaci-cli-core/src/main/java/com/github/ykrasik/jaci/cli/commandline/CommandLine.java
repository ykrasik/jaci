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

package com.github.ykrasik.jaci.cli.commandline;

import com.github.ykrasik.jaci.util.string.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * A pattern that matches spaces that aren't surrounded by single or double quotes.
     */
    private static final Pattern PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    private static List<String> splitCommandLine(String commandLine) {
        // Split the commandLine by whitespace.
        // Allow escaping single and double quoted strings.
        final List<String> matchList = new ArrayList<>();
        final Matcher matcher = PATTERN.matcher(Objects.requireNonNull(commandLine, "commandLine"));
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Add double-quoted string without the quotes.
                matchList.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Add single-quoted string without the quotes.
                matchList.add(matcher.group(2));
            } else {
                // Add unquoted word.
                matchList.add(matcher.group());
            }
        }
        return matchList;
    }
}

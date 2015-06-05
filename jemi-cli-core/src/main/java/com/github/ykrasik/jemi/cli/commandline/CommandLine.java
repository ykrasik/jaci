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

package com.github.ykrasik.jemi.cli.commandline;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandLine {
    private final String rawCommandLine;
    private final List<String> elements;

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public String getPathToCommand() {
        // The first element of the command line must be the path to a command.
        return elements.get(0);
    }

    public boolean hasCommandArgs() {
        return elements.size() > 1;
    }

    public List<String> getCommandArgs() {
        // The command args start from the 2nd arg.
        return elements.subList(1, elements.size());
    }

    @Override
    public String toString() {
        return rawCommandLine;
    }

    public static CommandLine forAssist(@NonNull String rawCommandLine) {
        final List<String> splitCommandLine = splitCommandLine(rawCommandLine);

        // If the commandLine ends with a space (or is empty), we manually insert an empty arg.
        // This implies that the user wanted assistance about the NEXT argument and not the last one that was typed.
        if (rawCommandLine.isEmpty() || rawCommandLine.endsWith(" ")) {
            splitCommandLine.add("");
        }

        return new CommandLine(rawCommandLine, splitCommandLine);
    }

    public static CommandLine forExecute(@NonNull String rawCommandLine) {
        final List<String> elements = splitCommandLine(rawCommandLine);
        return new CommandLine(rawCommandLine, elements);
    }

    // A pattern that matches spaces that aren't surrounded by single or double quotes.
    // FIXME: This pattern doesn't support named parameter calling with strings with whitespace (param="long string")
    // FIXME: Maybe the fix should not be in the pattern. Either way, calling long strings by name doesn't work.
    private static final Pattern PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    private static List<String> splitCommandLine(String commandLine) {
        // Split the commandLine by whitespace.
        // Allow escaping single and double quoted strings.
        final List<String> matchList = new ArrayList<>();
        final Matcher matcher = PATTERN.matcher(commandLine);
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

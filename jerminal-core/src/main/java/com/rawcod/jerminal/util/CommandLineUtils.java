package com.rawcod.jerminal.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 12:40
 */
public final class CommandLineUtils {
    // A pattern that regards sequential spaces as a single space
    private static final Pattern ARGS_PATTERN = Pattern.compile("[ ]+");

    private CommandLineUtils() {
    }

    public static List<String> splitCommandLineForAutoComplete(String commandLine) {
        final String trimmedCommandLine = trimCommandLineForAutoComplete(commandLine);
        return Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1));
    }

    public static List<String> splitCommandLineForExecute(String commandLine) {
        final String trimmedCommandLine = commandLine.trim();
        return Arrays.asList(ARGS_PATTERN.split(trimmedCommandLine, -1));
    }

    private static String trimCommandLineForAutoComplete(String commandLine) {
        // Only remove leading spaces, trailing spaces have a significant meaning.
        return commandLine.startsWith(" ") ?
            ARGS_PATTERN.matcher(commandLine).replaceFirst("") :
            commandLine;
    }
}

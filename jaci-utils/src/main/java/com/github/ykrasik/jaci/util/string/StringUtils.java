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

package com.github.ykrasik.jaci.util.string;

import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.List;

/**
 * Utilities for working with Strings.
 *
 * @author Yevgeny Krasik
 */
public final class StringUtils {
    private StringUtils() { }

    /**
     * Removes the leading and trailing delimiter from a string.
     *
     * @param str String to process.
     * @param delimiter Delimiter to remove.
     * @return The string with the leading and trailing delimiter removed.
     */
    public static String removeLeadingAndTrailingDelimiter(String str, String delimiter) {
        final int strLength = str.length();
        final int delimiterLength = delimiter.length();

        final boolean leadingDelimiter = str.startsWith(delimiter);
        final boolean trailingDelimiter = strLength > delimiterLength && str.endsWith(delimiter);
        if (!leadingDelimiter && !trailingDelimiter) {
            return str;
        } else {
            final int startingDelimiterIndex = leadingDelimiter ? delimiterLength : 0;
            final int endingDelimiterIndex = trailingDelimiter ? Math.max(strLength - delimiterLength, startingDelimiterIndex) : strLength;
            return str.substring(startingDelimiterIndex, endingDelimiterIndex);
        }
    }

    /**
     * Removes the trailing delimiter from a string.
     *
     * @param str String to process.
     * @param delimiter Delimiter to remove.
     * @return The string with the trailing delimiter removed.
     */
    public static String removeTrailingDelimiter(String str, String delimiter) {
        if (!str.endsWith(delimiter)) {
            return str;
        } else {
            return str.substring(0, str.length() - delimiter.length());
        }
    }

    /**
     * Removes the leading delimiter from a string.
     *
     * @param str String to process.
     * @param delimiter Delimiter to remove.
     * @return The string with the leading delimiter removed.
     */
    public static String removeLeadingDelimiter(String str, String delimiter) {
        if (!str.startsWith(delimiter)) {
            return str;
        } else {
            return str.substring(delimiter.length(), str.length());
        }
    }

    /**
     * Transforms an all-whitespace string to an {@code absent} value.
     *
     * @param str String to check.
     * @return A {@code present} containing the trimmed string, if the string had any non-whitespace character,
     *         or an {@code absent} value otherwise.
     */
    public static Opt<String> getNonEmptyString(String str) {
        return Opt.ofNullable(emptyToNull(str.trim()));
    }

    /**
     * Transform an empty string into null.
     *
     * @param str String to check.
     * @return The string if it was non-empty, or null otherwise.
     */
    public static String emptyToNull(String str) {
        return (str != null && !str.isEmpty()) ? str : null;
    }

    /**
     * Create a string out of the list's elements using the given delimiter.
     *
     * @param list List to create a string from.
     * @param delimiter Delimiter to use between elements.
     * @param <T> Type of elements in the list.
     * @return A string created from the given list's elements, delimited by the given delimiter.
     */
    public static <T> String join(List<T> list, String delimiter) {
        if (list.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (T element : list) {
            sb.append(element);
            sb.append(delimiter);
        }
        sb.delete(sb.length() - delimiter.length(), sb.length());
        return sb.toString();
    }
}

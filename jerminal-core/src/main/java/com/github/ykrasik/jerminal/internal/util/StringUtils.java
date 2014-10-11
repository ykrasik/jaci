/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.util;

/**
 * Utilities for working with Strings.
 *
 * @author Yevgeny Krasik
 */
public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Removes the leading and trailing delimiter from a string.
     *
     * @param str String to process.
     * @param delimiter Delimiter to remove.
     * @return The string with the leading and trailing delimiter removed.
     */
    public static String removeLeadingAndTrailingDelimiter(String str, String delimiter) {
        final int length = str.length();
        final boolean leadingDelimiter = str.startsWith(delimiter);
        final boolean trailingDelimiter = length > 1 && str.endsWith(delimiter);
        if (!leadingDelimiter && !trailingDelimiter) {
            return str;
        } else {
            final int startingDelimiterIndex = leadingDelimiter ? 1 : 0;
            final int endingDelimiterIndex = trailingDelimiter ? Math.max(length - 1, startingDelimiterIndex) : length;
            return str.substring(startingDelimiterIndex, endingDelimiterIndex);
        }
    }
}

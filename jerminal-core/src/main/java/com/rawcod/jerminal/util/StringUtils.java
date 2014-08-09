package com.rawcod.jerminal.util;

/**
 * User: ykrasik
 * Date: 09/08/2014
 * Time: 22:52
 */
public final class StringUtils {
    private StringUtils() {
    }

    public static String removeLeadingAndTrailing(String str, String strToRemove) {
        final int strLength = str.length();
        final int strToRemoveLength = strToRemove.length();

        final boolean leadingStr = str.startsWith(strToRemove);
        final int startingDelimiterIndex = leadingStr ? strToRemoveLength : 0;
        final boolean trailingStr = strLength >= strToRemoveLength && str.endsWith(strToRemove);
        final int endingDelimiterIndex = trailingStr ? Math.max(strLength - strToRemoveLength, startingDelimiterIndex) : strLength;
        if (!leadingStr && !trailingStr) {
            return str;
        } else {
            return str.substring(startingDelimiterIndex, endingDelimiterIndex);
        }
    }
}

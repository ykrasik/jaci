package com.rawcod.jerminal.util;

/**
 * User: ykrasik
 * Date: 22/07/2014
 * Time: 23:38
 */
public final class AutoCompleteUtils {
    private AutoCompleteUtils() {

    }

    public static String getAutoCompleteAddition(String rawArg, String autoCompletedArg) {
        return autoCompletedArg.substring(rawArg.length());
    }
}

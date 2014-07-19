package com.rawcod.jerminal.returnvalue.autocomplete;

import com.rawcod.jerminal.returnvalue.parse.ParseError;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 13:15
 */
public enum AutoCompleteError {
    PARSE_ERROR,
    INVALID_PATH,
    EMPTY_DIRECTORY,
    NO_POSSIBLE_VALUES,
    INTERNAL_ERROR;

    public static AutoCompleteError translateParseError(ParseError parseError) {
        switch (parseError) {
            case INVALID_ENTRY: return INVALID_PATH;
            case INTERNAL_ERROR: return INTERNAL_ERROR;
        }
    }
}

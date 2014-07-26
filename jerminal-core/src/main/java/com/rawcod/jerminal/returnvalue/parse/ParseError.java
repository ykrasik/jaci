package com.rawcod.jerminal.returnvalue.parse;

/**
 * User: ykrasik
 * Date: 12/01/14
 */
public enum ParseError {
    // Directory related
    EMPTY_DIRECTORY,
    ENTRY_DOES_NOT_EXIST,
    INVALID_ACCESS_TO_ENTRY,

    // Param related
    INVALID_PARAM,
    INVALID_PARAM_VALUE,
    PARAM_NOT_BOUND,
    PARAM_ALREADY_BOUND,

    INTERNAL_ERROR
}

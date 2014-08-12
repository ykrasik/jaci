package com.rawcod.jerminal.returnvalue.parse;

/**
 * User: ykrasik
 * Date: 12/01/14
 */
public enum ParseError {
    // Directory related
    EMPTY_DIRECTORY,
    INVALID_ENTRY,
    INVALID_ACCESS_TO_ENTRY,

    // Param related
    INVALID_PARAM,
    INVALID_PARAM_VALUE,
    PARAM_NOT_BOUND,
    PARAM_ALREADY_BOUND,
    NO_MORE_PARAMS,

    INTERNAL_ERROR
}

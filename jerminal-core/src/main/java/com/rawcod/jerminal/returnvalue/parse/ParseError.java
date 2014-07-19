package com.rawcod.jerminal.returnvalue.parse;

/**
 * User: ykrasik
 * Date: 12/01/14
 */
public enum ParseError {
    INVALID_ENTRY,
    INVALID_PATH,

    EMPTY_DIRECTORY,

    INVALID_PARAM,
    PARAM_ALREADY_BOUND,
    PARAM_NOT_BOUND,

    INTERNAL_ERROR
}

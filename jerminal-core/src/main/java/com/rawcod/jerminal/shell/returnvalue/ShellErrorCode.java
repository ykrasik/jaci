package com.rawcod.jerminal.shell.returnvalue;

/**
 * User: ykrasik
 * Date: 12/01/14
 */
public enum ShellErrorCode {
    NO_AUTO_COMPLETE_POSSIBILITIES,

    INVALID_COMMAND,
    INVALID_ARGUMENT,
    MISSING_ARGUMENT,
    EXCESS_ARGUMENT,

    COMMAND_EXECUTION_ERROR,

    INTERNAL_ERROR,
}

package com.rawcod.jerminal.exception;

/**
 * User: ykrasik
 * Date: 14/08/2014
 * Time: 19:09
 */
public class ExecuteException extends Exception {
    public ExecuteException(String message) {
        super(message);
    }

    public ExecuteException(String format, Object... args) {
        this(String.format(format, args));
    }
}
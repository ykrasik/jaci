package com.rawcod.jerminal.exception;

/**
 * User: ykrasik
 * Date: 22/07/2014
 * Time: 22:39
 */
public class ShellException extends RuntimeException {
    public ShellException() {
    }

    public ShellException(String format, Object... args) {
        super(String.format(format, args));
    }

    public ShellException(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }
}

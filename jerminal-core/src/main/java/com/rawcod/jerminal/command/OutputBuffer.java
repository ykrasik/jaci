package com.rawcod.jerminal.command;

/**
 * User: ykrasik
 * Date: 14/08/2014
 * Time: 19:17
 */
public interface OutputBuffer {
    void println(String message);
    void println(String format, Object... args);
}

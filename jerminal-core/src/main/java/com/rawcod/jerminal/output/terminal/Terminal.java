package com.rawcod.jerminal.output.terminal;

/**
 * User: ykrasik
 * Date: 05/08/2014
 * Time: 00:28
 */
public interface Terminal {
    void clearCommandLine();
    void setCommandLine(String commandLine);

    void print(String message);
    void printError(String message);
}

package com.rawcod.jerminal;

import com.rawcod.jerminal.shell.entry.directory.ShellTree;

import java.util.List;

/**
 * User: yevgenyk
 * Date: 07/01/14
 *
 * A terminal is how information is presented to the user.
 * In an MVC model, the terminal would be equivalent to the view.
 */
public interface Terminal {
    void displayMessage(String message);
    void displayCommandReturnMessage(String message);
    void displayError(String error);
    void displayUsage(String usage);
    void displaySuggestions(List<String> suggestions);
    void displayShellTree(ShellTree root);

    void setCommandLine(String commandLine);
    void setCommandLineCursor(int index);

    void setCurrentPath(List<String> path);
}

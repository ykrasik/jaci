package com.rawcod.jerminal.output;

import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 11:58
 */
public interface OutputProcessor {
    void clearCommandLine();
    void setCommandLine(String commandLine);

    void handleBlankCommandLine();

    void parseError(ParseError error, String errorMessage);
    void autoCompleteNotPossible(String errorMessage);

    void executeError(String errorMessage);
    void executeUnhandledException(Exception e);

    void displaySuggestions(List<String> directorySuggestions,
                            List<String> commandSuggestions,
                            List<String> paramNameSuggestions,
                            List<String> paramValueSuggestions);
    void displayCommandOutput(List<String> output);

    void displayShellEntryView(ShellEntryView shellEntryView);
    void displayShellCommandView(ShellCommandView shellCommandView);
}

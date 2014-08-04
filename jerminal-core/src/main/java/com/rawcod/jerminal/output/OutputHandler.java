package com.rawcod.jerminal.output;

import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.execute.ExecuteError;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 11:58
 */
public interface OutputHandler {
    void clearCommandLine();
    void setCommandLine(String commandLine);

    void handleBlankCommandLine();

    void parseError(ParseError error, String errorMessage);
    void autoCompleteError(AutoCompleteError error, String errorMessage);
    void executeError(ExecuteError error, String errorMessage);
    void executeUnhandledException(Exception e);

    void displaySuggestions(List<String> suggestions);
    void displayCommandOutput(List<String> output);

    void displayShellEntryView(ShellEntryView shellEntryView);
    void displayShellCommandView(ShellCommandView shellCommandView);
}

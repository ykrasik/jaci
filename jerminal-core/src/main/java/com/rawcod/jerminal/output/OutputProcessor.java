package com.rawcod.jerminal.output;

import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import java.util.List;

/**
 * User: ykrasik
 * Date: 22/07/2014
 * Time: 23:55
 */
public class OutputProcessor {
    private final OutputHandler outputHandler;

    public OutputProcessor(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }

    public void clearCommandLine() {
        outputHandler.clearCommandLine();
    }

    public void setCommandLine(String commandLine) {
        outputHandler.setCommandLine(commandLine);
    }

    public void blankCommandLine() {
        outputHandler.handleBlankCommandLine();
    }

    public void parseFailure(ParseReturnValueFailure failure) {
        outputHandler.parseError(failure.getError(), failure.getMessage());
    }

    public void autoCompleteSuccess(AutoCompleteReturnValueSuccess success, String rawCommandLine) {
        final String autoCompleteAddition = success.getAutoCompleteAddition();
        if (!autoCompleteAddition.isEmpty()) {
            // Set commandLine to autoCompleted commandLine.
            final String newCommandLine = rawCommandLine + autoCompleteAddition;
            outputHandler.setCommandLine(newCommandLine);
        }

        displaySuggestionsIfApplicable(success.getSuggestions());
    }

    public void autoCompleteFailure(AutoCompleteReturnValueFailure failure) {
        if (failure.getParseError().isPresent()) {
            outputHandler.parseError(failure.getParseError().get(), failure.getMessage());
        } else {
            outputHandler.autoCompleteError(failure.getError(), failure.getMessage());
        }

        displaySuggestionsIfApplicable(failure.getSuggestions());
    }

    public void executeSuccess(ExecuteReturnValueSuccess success) {
        displayCommandOutputIfApplicable(success.getOutput());
    }

    public void executeFailure(ExecuteReturnValueFailure failure) {
        if (failure.getException().isPresent()) {
            outputHandler.executeUnhandledException(failure.getException().get());
        } else {
            outputHandler.executeError(failure.getError(), failure.getErrorMessage());
        }

        displayCommandOutputIfApplicable(failure.getOutput());
    }

    public void processShellEntryView(ShellEntryView shellEntryView) {
        outputHandler.displayShellEntryView(shellEntryView);
    }

    public void processShellCommandView(ShellCommandView shellCommandView) {
        outputHandler.displayShellCommandView(shellCommandView);
    }

    private void displaySuggestionsIfApplicable(List<String> suggestions) {
        if (!suggestions.isEmpty()) {
            outputHandler.displaySuggestions(suggestions);
        }
    }

    private void displayCommandOutputIfApplicable(List<String> output) {
        if (!output.isEmpty()) {
            outputHandler.displayCommandOutput(output);
        }
    }
}

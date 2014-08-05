package com.rawcod.jerminal.output;

import com.google.common.base.Optional;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteError;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
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
        clearCommandLine();
        outputHandler.handleBlankCommandLine();
    }

    public void parseFailure(ParseReturnValueFailure failure) {
        final ParseError error = failure.getError();
        final String errorMessage = failure.getErrorMessage();
        outputHandler.parseError(error, errorMessage);
    }

    public void autoCompleteSuccess(String newCommandLine, List<String> suggestions) {
        outputHandler.setCommandLine(newCommandLine);

        displaySuggestionsIfApplicable(suggestions);
    }

    public void autoCompleteFailure(AutoCompleteReturnValueFailure failure) {
        final String errorMessage = failure.getErrorMessage();
        final Optional<ParseError> parseError = failure.getParseError();
        if (parseError.isPresent()) {
            outputHandler.parseError(parseError.get(), errorMessage);
        } else {
            final AutoCompleteError error = failure.getError();
            outputHandler.autoCompleteError(error, errorMessage);
        }

        final List<String> suggestions = failure.getSuggestions();
        displaySuggestionsIfApplicable(suggestions);
    }

    public void executeSuccess(ExecuteReturnValueSuccess success) {
        outputHandler.clearCommandLine();

        final List<String> output = success.getOutput();
        displayCommandOutputIfApplicable(output);
    }

    public void executeFailure(ExecuteReturnValueFailure failure) {
        outputHandler.clearCommandLine();

        final Optional<Exception> exception = failure.getException();
        if (exception.isPresent()) {
            outputHandler.executeUnhandledException(exception.get());
        } else {
            final ExecuteError error = failure.getError();
            final String errorMessage = failure.getErrorMessage();
            outputHandler.executeError(error, errorMessage);
        }

        final List<String> output = failure.getOutput();
        displayCommandOutputIfApplicable(output);
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

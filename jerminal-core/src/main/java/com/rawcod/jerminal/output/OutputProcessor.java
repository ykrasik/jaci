package com.rawcod.jerminal.output;

import com.google.common.base.Optional;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.returnvalue.execute.ExecuteError;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.suggestion.Suggestions;

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

    public void parseError(ParseError error, String message, Optional<Suggestions> suggestions) {
        outputHandler.parseError(error, message);
        displaySuggestionsIfApplicable(suggestions);
    }

    public void autoCompleteSuccess(String newCommandLine, Optional<Suggestions> suggestions) {
        outputHandler.setCommandLine(newCommandLine);

        displaySuggestionsIfApplicable(suggestions);
    }

    public void autoCompleteNotPossible(String message) {
        outputHandler.autoCompleteNotPossible(message);
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

    private void displaySuggestionsIfApplicable(Optional<Suggestions> suggestions) {
        if (suggestions.isPresent()) {
            displaySuggestions(suggestions.get());
        }
    }

    private void displaySuggestions(Suggestions suggestions) {
        final List<String> directorySuggestions = suggestions.getDirectorySuggestions();
        final List<String> commandSuggestions = suggestions.getCommandSuggestions();
        final List<String> paramNameSuggestions = suggestions.getParamNameSuggestions();
        final List<String> paramValueSuggestions = suggestions.getParamValueSuggestions();

        outputHandler.displaySuggestions(
            directorySuggestions,
            commandSuggestions,
            paramNameSuggestions,
            paramValueSuggestions
        );
    }

    private void displayCommandOutputIfApplicable(List<String> output) {
        if (!output.isEmpty()) {
            outputHandler.displayCommandOutput(output);
        }
    }
}

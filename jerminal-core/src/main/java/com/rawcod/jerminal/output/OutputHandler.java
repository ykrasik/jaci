package com.rawcod.jerminal.output;

import com.google.common.base.Optional;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 22/07/2014
 * Time: 23:55
 */
public class OutputHandler {
    private final List<OutputProcessor> outputProcessors;

    public OutputHandler() {
        this.outputProcessors = new ArrayList<>();
    }

    public void add(OutputProcessor outputProcessor) {
        outputProcessors.add(outputProcessor);
    }

    public void clearCommandLine() {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.clearCommandLine();
        }
    }

    public void setCommandLine(String commandLine) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.setCommandLine(commandLine);
        }
    }

    public void handleParseFailure(ParseReturnValueFailure failure) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.handleParseFailure(failure);
        }
    }

    public void displayAutoCompleteSuggestions(List<String> suggestions) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.displayAutoCompleteSuggestions(suggestions);
        }
    }

    public void handleAutoCompleteFailure(AutoCompleteReturnValueFailure failure) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.handleAutoCompleteFailure(failure);
        }
    }

    public void handleExecuteSuccess(ExecuteReturnValueSuccess success) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.handleExecuteCommandSuccess(output, returnValue);
        }
    }

    public void handleExecuteFailure(ExecuteReturnValueFailure failure) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            outputProcessor.handleExecuteCommandFailure(failure);
        }
    }

    public void displayShellEntryView(ShellEntryView shellEntryView) {

    }

    public void displayShellCommandView(ShellCommandView shellCommandView) {

    }
}

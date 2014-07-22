package com.rawcod.jerminal.output;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueFailure;

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
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.clearCommandLine();
            }
        });
    }

    public void setCommandLine(final String commandLine) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.setCommandLine(commandLine);
            }
        });
    }

    public void println(final String message) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.println(message);
            }
        });
    }

    public void handleAutoCompleteSuccess(final String newCommandLine, final List<String> possibilities) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processAutoCompleteSuccess(newCommandLine, possibilities);
            }
        });
    }

    public void handleAutoCompleteFailure(final AutoCompleteReturnValueFailure failure) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processAutoCompleteFailure(failure);
            }
        });
    }

    public void handleExecuteSuccess(final String output, final Optional<Object> returnValue) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processExecuteOutputSuccess(output, returnValue);
            }
        });
    }

    public void handleExecuteFailure(final ExecuteReturnValueFailure failure) {
        forEachOutputProcessor(new OutputProcessorTask() {
            @Override
            public void process(OutputProcessor outputProcessor) {
                outputProcessor.processExecuteOutputFailure(failure);
            }
        });
    }

    private void forEachOutputProcessor(OutputProcessorTask task) {
        for (OutputProcessor outputProcessor : outputProcessors) {
            task.process(outputProcessor);
        }
    }

    private interface OutputProcessorTask {
        void process(OutputProcessor outputProcessor);
    }
}

package com.rawcod.jerminal.returnvalue.execute.flow;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.FailureImpl;
import com.rawcod.jerminal.returnvalue.execute.ExecuteError;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 16/07/2014
 * Time: 21:19
 */
public class ExecuteReturnValueFailure extends FailureImpl {
    private final ExecuteError error;
    private final String errorMessage;
    private final List<String> output;
    private final Optional<Exception> exception;

    public ExecuteReturnValueFailure(ExecuteError error,
                                     String errorMessage,
                                     List<String> output,
                                     Optional<Exception> exception) {
        this.error = checkNotNull(error, "error");
        this.errorMessage = checkNotNull(errorMessage, "errorMessage");
        this.output = checkNotNull(output, "output");
        this.exception = checkNotNull(exception, "exception");
    }

    public ExecuteError getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getOutput() {
        return output;
    }

    public Optional<Exception> getException() {
        return exception;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("errorMessage", errorMessage)
            .add("output", output)
            .add("exception", exception)
            .toString();
    }
}

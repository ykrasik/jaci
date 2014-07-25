package com.rawcod.jerminal.returnvalue.execute;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 16/07/2014
 * Time: 21:19
 */
public class ExecuteReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ExecuteError error;
    private final String errorMessage;
    private final Optional<Exception> exception;
    private final String output;
    private final List<String> suggestions;

    private ExecuteReturnValueFailure(Builder builder) {
        this.error = checkNotNull(builder.executeError, "error is null!");
        this.errorMessage = checkNotNull(builder.message, "errorMessage is null!");
        this.exception = checkNotNull(builder.exception, "exception is null!");
        this.output = checkNotNull(builder.commandOutput, "output is null!");
        this.suggestions = checkNotNull(builder.suggestion, "suggestions is null!");
    }

    public ExecuteError getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Optional<Exception> getException() {
        return exception;
    }

    public String getOutput() {
        return output;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("parseError", parseError)
            .add("errorMessage", errorMessage)
            .add("exception", exception)
            .add("output", output)
            .add("suggestions", suggestions)
            .toString();
    }

    public static class Builder {
        private final ExecuteError executeError;
        private Optional<ParseError> parseError = Optional.absent();
        private Optional<String> message = Optional.absent();
        private Optional<Exception> exception = Optional.absent();
        private ShellSuggestion suggestion = ShellSuggestion.none(); // TODO: Make this optional?
        private String commandOutput = "";

        Builder(ExecuteError executeError) {
            this.executeError = executeError;
        }

        public ExecuteReturnValue build() {
            final ExecuteReturnValueFailure failure = new ExecuteReturnValueFailure(this);
            return new ExecuteReturnValue(failure);
        }

        public Builder setParseError(ParseError parseError) {
            this.parseError = Optional.of(parseError);
            return this;
        }

        public Builder setMessage(String message) {
            this.message = Optional.of(message);
            return this;
        }

        public Builder setMessage(Optional<String> message) {
            this.message = message;
            return this;
        }

        public Builder setMessageFormat(String format, Object... args) {
            return setMessage(String.format(format, args));
        }

        public Builder setException(Exception exception) {
            this.exception = Optional.of(exception);
            return this;
        }

        public Builder setException(Optional<Exception> exception) {
            this.exception = exception;
            return this;
        }

        public Builder setSuggestion(ShellSuggestion suggestion) {
            this.suggestion = suggestion;
            return this;
        }

        public Builder setCommandOutput(String commandOutput) {
            this.commandOutput = commandOutput;
            return this;
        }
    }
}

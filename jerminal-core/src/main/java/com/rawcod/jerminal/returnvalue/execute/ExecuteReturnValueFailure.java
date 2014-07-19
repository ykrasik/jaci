package com.rawcod.jerminal.returnvalue.execute;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 16/07/2014
 * Time: 21:19
 */
public class ExecuteReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ExecuteError executeError;
    private final Optional<ParseError> parseError;
    private final Optional<String> message;
    private final Optional<Exception> exception;
    private final ShellSuggestion suggestion;
    private final String commandOutput;

    private ExecuteReturnValueFailure(Builder builder) {
        this.executeError = checkNotNull(builder.executeError, "executeError is null!");
        this.parseError = checkNotNull(builder.parseError, "parseError is null!");
        this.message = checkNotNull(builder.message, "message is null!");
        this.exception = checkNotNull(builder.exception, "exception is null!");
        this.suggestion = checkNotNull(builder.suggestion, "suggestion is null!");
        this.commandOutput = checkNotNull(builder.commandOutput, "commandOutput is null!");
    }

    public ExecuteError getExecuteError() {
        return executeError;
    }

    public Optional<ParseError> getParseError() {
        return parseError;
    }

    public Optional<String> getMessage() {
        return message;
    }

    public Optional<Exception> getException() {
        return exception;
    }

    public ShellSuggestion getSuggestion() {
        return suggestion;
    }

    public String getCommandOutput() {
        return commandOutput;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("parseError", parseError)
            .add("message", message)
            .add("exception", exception)
            .add("suggestion", suggestion)
            .add("commandOutput", commandOutput)
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

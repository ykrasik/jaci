package com.rawcod.jerminal.returnvalue.parse.flow;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 17/07/2014
 * Time: 10:19
 */
public class ParseReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ParseError error;
    private final Optional<String> message;
    private final Optional<Exception> exception;
    private final ShellSuggestion suggestion;  // TODO: Make this optional?

    private ParseReturnValueFailure(Builder builder) {
        this.error = checkNotNull(builder.error, "error is null!");
        this.message = checkNotNull(builder.message, "message is null!");
        this.exception = checkNotNull(builder.exception, "exception is null!");
        this.suggestion = checkNotNull(builder.suggestion, "suggestion is null!");
    }

    public ParseError getError() {
        return error;
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("message", message)
            .add("exception", exception)
            .add("suggestion", suggestion)
            .toString();
    }

    public static class Builder {
        private final ParseError error;
        private Optional<String> message = Optional.absent();
        private Optional<Exception> exception = Optional.absent();
        private ShellSuggestion suggestion = ShellSuggestion.none();

        Builder(ParseError error) {
            this.error = error;
        }

        public ParseReturnValue build() {
            final ParseReturnValueFailure failure = new ParseReturnValueFailure(this);
            return new ParseReturnValue(failure);
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

        public Builder setSuggestion(ShellSuggestion suggestion) {
            this.suggestion = suggestion;
            return this;
        }
    }
}

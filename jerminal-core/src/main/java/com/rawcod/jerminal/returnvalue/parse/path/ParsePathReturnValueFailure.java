package com.rawcod.jerminal.returnvalue.parse.path;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:56
 */
public class ParsePathReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ParseError error;
    private final Optional<String> message;
    private final ShellSuggestion suggestion;

    private ParsePathReturnValueFailure(Builder builder) {
        this.error = checkNotNull(builder.error, "error is null!");
        this.message = checkNotNull(builder.message, "message is null!");
        this.suggestion = checkNotNull(builder.suggestion, "suggestion is null!");
    }

    public ParseError getError() {
        return error;
    }

    public Optional<String> getMessage() {
        return message;
    }

    public ShellSuggestion getSuggestion() {
        return suggestion;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("message", message)
            .add("suggestion", suggestion)
            .toString();
    }

    public static class Builder {
        private final ParseError error;
        private Optional<String> message = Optional.absent();
        private ShellSuggestion suggestion = ShellSuggestion.none();

        Builder(ParseError error) {
            this.error = error;
        }

        public ParsePathReturnValue build() {
            final ParsePathReturnValueFailure failure = new ParsePathReturnValueFailure(this);
            return new ParsePathReturnValue(failure);
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

        public Builder setSuggestion(ShellSuggestion suggestion) {
            this.suggestion = suggestion;
            return this;
        }
    }
}

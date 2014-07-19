package com.rawcod.jerminal.returnvalue.parse.args;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:07
 */
public class ParseCommandArgsReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ParseError error;
    private final Optional<String> message;
    private final ShellSuggestion suggestion;

    private ParseCommandArgsReturnValueFailure(Builder builder) {
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

        public ParseCommandArgsReturnValue build() {
            final ParseCommandArgsReturnValueFailure failure = new ParseCommandArgsReturnValueFailure(this);
            return new ParseCommandArgsReturnValue(failure);
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

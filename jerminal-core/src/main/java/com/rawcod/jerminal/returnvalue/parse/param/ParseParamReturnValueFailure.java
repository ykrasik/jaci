package com.rawcod.jerminal.returnvalue.parse.param;

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
public class ParseParamReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ParseError error;
    private final Optional<String> message;
    private final ShellSuggestion suggestion;

    private ParseParamReturnValueFailure(Builder builder) {
        this.error = checkNotNull(builder.error, "Error is null!");
        this.message = checkNotNull(builder.message, "Message is null!");
        this.suggestion = checkNotNull(builder.suggestion, "Suggestion is null!");
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

        public ParseParamReturnValue build() {
            final ParseParamReturnValueFailure failure = new ParseParamReturnValueFailure(this);
            return new ParseParamReturnValue(failure);
        }

        public Builder setMessage(String message) {
            this.message = Optional.of(message);
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

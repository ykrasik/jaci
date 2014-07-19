package com.rawcod.jerminal.returnvalue.autocomplete.flow;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 13:14
 */
public class AutoCompleteReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final AutoCompleteError error;
    private final Optional<ParseError> parseError;
    private final Optional<String> message;
    private final Optional<Exception> exception;

    private AutoCompleteReturnValueFailure(Builder builder) {
        this.error = checkNotNull(builder.error, "error is null!");
        this.parseError = checkNotNull(builder.parseError, "parseError is null!");
        this.message = checkNotNull(builder.message, "message is null!");
        this.exception = checkNotNull(builder.exception, "exception is null!");
    }

    public AutoCompleteError getError() {
        return error;
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("parseError", parseError)
            .add("message", message)
            .add("exception", exception)
            .toString();
    }

    public static class Builder {
        private final AutoCompleteError error;
        private Optional<ParseError> parseError;
        private Optional<String> message = Optional.absent();
        private Optional<Exception> exception = Optional.absent();

        Builder(AutoCompleteError error) {
            this.error = error;
        }

        public AutoCompleteReturnValue build() {
            final AutoCompleteReturnValueFailure failure = new AutoCompleteReturnValueFailure(this);
            return new AutoCompleteReturnValue(failure);
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
    }
}

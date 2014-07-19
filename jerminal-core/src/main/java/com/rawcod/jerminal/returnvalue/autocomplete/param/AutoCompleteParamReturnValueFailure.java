package com.rawcod.jerminal.returnvalue.autocomplete.param;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:48
 */
public class AutoCompleteParamReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final AutoCompleteError error;
    private final Optional<String> message;

    private AutoCompleteParamReturnValueFailure(Builder builder) {
        this.error = checkNotNull(builder.error, "Error is null!");
        this.message = checkNotNull(builder.message, "Message is null!");
    }

    public AutoCompleteError getError() {
        return error;
    }

    public Optional<String> getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("message", message)
            .toString();
    }

    public static class Builder {
        private final AutoCompleteError error;
        private Optional<String> message = Optional.absent();

        Builder(AutoCompleteError error) {
            this.error = error;
        }

        public AutoCompleteParamReturnValue build() {
            final AutoCompleteParamReturnValueFailure failure = new AutoCompleteParamReturnValueFailure(this);
            return new AutoCompleteParamReturnValue(failure);
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
    }
}
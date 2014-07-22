package com.rawcod.jerminal.returnvalue.execute;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 16/07/2014
 * Time: 21:19
 */
public class ExecuteReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final String output;
    private final Optional<Object> returnValue;

    private ExecuteReturnValueSuccess(Builder builder) {
        this.message = checkNotNull(builder.message, "Message is null!");
        this.returnValue = checkNotNull(builder.returnValue, "Return value is null!");
    }

    public String getOutput() {
        return output;
    }

    public Optional<Object> getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("message", message)
            .add("returnValue", returnValue)
            .toString();
    }

    public static class Builder {
        private Optional<String> message = Optional.absent();
        private Optional<Object> returnValue = Optional.absent();

        Builder() {

        }

        public ExecuteReturnValue build() {
            final ExecuteReturnValueSuccess success = new ExecuteReturnValueSuccess(this);
            return new ExecuteReturnValue(success);
        }

        public Builder setMessage(String message) {
            this.message = Optional.of(message);
            return this;
        }

        public Builder setMessageFormat(String format, Object... args) {
            return setMessage(String.format(format, args));
        }

        public Builder setReturnValue(Object returnValue) {
            this.returnValue = Optional.of(returnValue);
            return this;
        }
    }
}

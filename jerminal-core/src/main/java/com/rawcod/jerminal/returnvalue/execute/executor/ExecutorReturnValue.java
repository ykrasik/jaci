package com.rawcod.jerminal.returnvalue.execute.executor;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.FailureImpl;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue.ExecutorReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue.ExecutorReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class ExecutorReturnValue extends ReturnValueImpl<ExecutorReturnValueSuccess, ExecutorReturnValueFailure> {
    private ExecutorReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ExecutorReturnValue success() {
        return new ExecutorReturnValue(new ExecutorReturnValueSuccess(Optional.absent()));
    }

    public static ExecutorReturnValue success(Object returnValue) {
        return new ExecutorReturnValue(new ExecutorReturnValueSuccess(Optional.of(returnValue)));
    }

    public static ExecutorReturnValue failure(String errorMessage) {
        return new ExecutorReturnValue(new ExecutorReturnValueFailure(errorMessage, Optional.<Exception>absent()));
    }

    public static ExecutorReturnValue failure(String format, Object... args) {
        return failure(String.format(format, args));
    }

    public static ExecutorReturnValue failure(Exception e, String errorMessage) {
        return new ExecutorReturnValue(new ExecutorReturnValueFailure(errorMessage, Optional.of(e)));
    }

    public static ExecutorReturnValue failure(Exception e, String format, Object... args) {
        return failure(e, String.format(format, args));
    }


    public static class ExecutorReturnValueSuccess extends SuccessImpl {
        private final Optional<Object> returnValue;

        private ExecutorReturnValueSuccess(Optional<Object> returnValue) {
            this.returnValue = checkNotNull(returnValue, "returnValue");
        }

        public Optional<Object> getReturnValue() {
            return returnValue;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("returnValue", returnValue)
                .toString();
        }
    }

    public static class ExecutorReturnValueFailure extends FailureImpl {
        private final String errorMessage;
        private final Optional<Exception> exception;

        private ExecutorReturnValueFailure(String errorMessage, Optional<Exception> exception) {
            this.errorMessage = checkNotNull(errorMessage, "errorMessage");
            this.exception = checkNotNull(exception, "exception");
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Optional<Exception> getException() {
            return exception;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("errorMessage", errorMessage)
                .add("exception", exception)
                .toString();
        }
    }
}

package com.rawcod.jerminal.returnvalue;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.ReturnValue.Failure;
import com.rawcod.jerminal.returnvalue.ReturnValue.Success;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 16:51
 */
public class ReturnValueImpl<S extends Success, F extends Failure> implements ReturnValue<S, F> {
    private final Failable returnValue;

    public ReturnValueImpl(Failable returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public boolean isSuccess() {
        return returnValue.isSuccess();
    }

    @Override
    public boolean isFailure() {
        return !isSuccess();
    }

    @SuppressWarnings("unchecked")
    @Override
    public S getSuccess() {
        checkArgument(isSuccess(), "Trying to call getSuccess on a failure returnValue: %s", returnValue);
        return (S) returnValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public F getFailure() {
        checkArgument(!isSuccess(), "Trying to call getFailure on a success returnValue: %s", returnValue);
        return (F) returnValue;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("returnValue", returnValue)
            .toString();
    }

    public static class SuccessImpl implements Success {
        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    public static class FailureImpl implements Failure {
        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}

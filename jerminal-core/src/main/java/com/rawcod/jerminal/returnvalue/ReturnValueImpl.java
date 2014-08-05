package com.rawcod.jerminal.returnvalue;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 16:51
 */
public class ReturnValueImpl<S extends Success, F extends Failure> implements ReturnValue<S, F> {
    private final Failable returnValue;

    public ReturnValueImpl(Failable returnValue) {
        this.returnValue = checkNotNull(returnValue, "returnValue");
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
        assertSuccess();
        return (S) returnValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public F getFailure() {
        assertFailure();
        return (F) returnValue;
    }

    @SuppressWarnings("unchecked")
    public <F2 extends Failure> ReturnValueImpl<S, F2> toSuccess() {
        assertSuccess();
        return (ReturnValueImpl<S, F2>) this;
    }

    @SuppressWarnings("unchecked")
    public <S2 extends Success> ReturnValueImpl<S2, F> toFailure() {
        assertFailure();
        return (ReturnValueImpl<S2, F>) this;
    }

    private void assertSuccess() {
        checkArgument(isSuccess(), "Trying to use a failure returnValue as success: %s", returnValue);
    }

    private void assertFailure() {
        checkArgument(!isSuccess(), "Trying to use a success returnValue as failure: %s", returnValue);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("returnValue", returnValue)
            .toString();
    }

}

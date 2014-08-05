package com.rawcod.jerminal.returnvalue.execute.flow;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.SuccessImpl;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 16/07/2014
 * Time: 21:19
 */
public class ExecuteReturnValueSuccess extends SuccessImpl {
    private final Optional<Object> returnValue;
    private final List<String> output;

    public ExecuteReturnValueSuccess(Optional<Object> returnValue, List<String> output) {
        this.returnValue = checkNotNull(returnValue, "returnValue");
        this.output = checkNotNull(output, "output");
    }

    public Optional<Object> getReturnValue() {
        return returnValue;
    }

    public List<String> getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("output", output)
            .add("returnValue", returnValue)
            .toString();
    }
}

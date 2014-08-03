package com.rawcod.jerminal.returnvalue.execute.flow;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.execute.ExecuteError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class ExecuteReturnValue extends ReturnValueImpl<ExecuteReturnValueSuccess, ExecuteReturnValueFailure> {
    private ExecuteReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ExecuteReturnValue success(Optional<Object> returnValue, List<String> output) {
        return new ExecuteReturnValue(new ExecuteReturnValueSuccess(returnValue, output));
    }

    public static ExecuteReturnValue failure(String errorMessage, List<String> output) {
        return new ExecuteReturnValue(new ExecuteReturnValueFailure(
            ExecuteError.EXECUTION_FAILURE, errorMessage, output, Optional.<Exception>absent()
        ));
    }

    public static ExecuteReturnValue failureException(Exception e, List<String> output) {
        return new ExecuteReturnValue(new ExecuteReturnValueFailure(
            ExecuteError.UNHANDLED_EXCEPTION, "Command terminated due to an unhandled exception.", output, Optional.of(e)
        ));
    }
}

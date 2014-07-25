package com.rawcod.jerminal.returnvalue.execute;

import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class ExecuteReturnValue extends ReturnValueImpl<ExecuteReturnValueSuccess, ExecuteReturnValueFailure> {
    ExecuteReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ExecuteReturnValueSuccess.Builder successBuilder() {
        return new ExecuteReturnValueSuccess.Builder();
    }

    public static ExecuteReturnValueFailure.Builder failureBuilder(ExecuteError error) {
        return new ExecuteReturnValueFailure.Builder(error);
    }

    public static ExecuteReturnValue parseFailure(ParseReturnValueFailure failure) {

    }

    public static ExecuteReturnValue failureFrom(ParseReturnValueFailure failure) {
        return failureBuilder(ExecuteError.PARSE_ERROR)
            .setParseError(failure.getError())
            .setMessage(failure.getMessage())
            .setException(failure.getException())
            .setSuggestion(failure.getSuggestions())
            .build();
    }
}

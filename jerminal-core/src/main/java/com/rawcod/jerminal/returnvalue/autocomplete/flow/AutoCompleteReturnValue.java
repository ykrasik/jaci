package com.rawcod.jerminal.returnvalue.autocomplete.flow;

import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.path.AutoCompletePathReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValueFailure;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 08/01/14
 */
public class AutoCompleteReturnValue extends ReturnValueImpl<AutoCompleteReturnValueSuccess, AutoCompleteReturnValueFailure> {
    AutoCompleteReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static AutoCompleteReturnValue successSingle(String newPath) {
        return successMultiple(newPath, Collections.<String>emptyList());
    }

    public static AutoCompleteReturnValue successMultiple(String newPath, List<String> possibilities) {
        final AutoCompleteReturnValueSuccess success = new AutoCompleteReturnValueSuccess(newPath, possibilities);
        return new AutoCompleteReturnValue(success);
    }

    public static AutoCompleteReturnValueFailure.Builder failureBuilder(AutoCompleteError error) {
        return new AutoCompleteReturnValueFailure.Builder(error);
    }

    public static AutoCompleteReturnValue failureFrom(ParsePathReturnValueFailure failure) {
        final AutoCompleteError error = AutoCompleteError.translateParseError(failure.getError());
        return failureBuilder(error)
            .setMessage(failure.getMessage())
            .build();
    }

    public static AutoCompleteReturnValue failureFrom(AutoCompletePathReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .build();
    }

    public static AutoCompleteReturnValue failureFrom(ParsePartialCommandArgsReturnValueFailure failure) {
        return null;
    }

    public static AutoCompleteReturnValue failureFrom(AutoCompleteParamReturnValueFailure failure) {
        return null;
    }
}

package com.rawcod.jerminal.returnvalue.autocomplete.param;

import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:46
 */
public class AutoCompleteParamReturnValue extends ReturnValueImpl<AutoCompleteParamReturnValueSuccess, AutoCompleteParamReturnValueFailure> {
    AutoCompleteParamReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static AutoCompleteParamReturnValue success(ShellSuggestion suggestion) {
        final AutoCompleteParamReturnValueSuccess success = new AutoCompleteParamReturnValueSuccess(suggestion);
        return new AutoCompleteParamReturnValue(success);
    }

    public static AutoCompleteParamReturnValueFailure.Builder failureBuilder(AutoCompleteError error) {
        return new AutoCompleteParamReturnValueFailure.Builder(error);
    }
}
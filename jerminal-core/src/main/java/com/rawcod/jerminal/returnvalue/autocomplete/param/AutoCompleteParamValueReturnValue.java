package com.rawcod.jerminal.returnvalue.autocomplete.param;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamValueReturnValue.AutoCompleteParamValueReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:46
 */
public class AutoCompleteParamValueReturnValue extends ReturnValueImpl<AutoCompleteParamValueReturnValueSuccess, AutoCompleteReturnValueFailure> {
    private AutoCompleteParamValueReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static AutoCompleteParamValueReturnValue success(ShellSuggestion suggestion) {
        return new AutoCompleteParamValueReturnValue(new AutoCompleteParamValueReturnValueSuccess(suggestion));
    }

    public static AutoCompleteParamValueReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompleteParamValueReturnValue(failure);
    }

    public static AutoCompleteParamValueReturnValue parseFailure(ParseReturnValueFailure failure) {
        return new AutoCompleteParamValueReturnValue(AutoCompleteReturnValueFailure.parseFailure(failure));
    }


    public static class AutoCompleteParamValueReturnValueSuccess extends SuccessImpl {
        private final ShellSuggestion suggestion;

        private AutoCompleteParamValueReturnValueSuccess(ShellSuggestion suggestion) {
            this.suggestion = checkNotNull(suggestion, "suggestion is null!");
        }

        public ShellSuggestion getSuggestion() {
            return suggestion;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("suggestion", suggestion)
                .toString();
        }
    }
}
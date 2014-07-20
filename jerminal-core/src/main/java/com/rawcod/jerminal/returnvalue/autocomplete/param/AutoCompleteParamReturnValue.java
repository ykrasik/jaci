package com.rawcod.jerminal.returnvalue.autocomplete.param;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamReturnValue.AutoCompleteParamReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:46
 */
public class AutoCompleteParamReturnValue extends ReturnValueImpl<AutoCompleteParamReturnValueSuccess, AutoCompleteReturnValueFailure> {
    private AutoCompleteParamReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static AutoCompleteParamReturnValue success(ShellSuggestion suggestion) {
        return new AutoCompleteParamReturnValue(new AutoCompleteParamReturnValueSuccess(suggestion));
    }

    public static AutoCompleteParamReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompleteParamReturnValue(failure);
    }

    public static AutoCompleteParamReturnValue parseFailure(ParseReturnValueFailure failure) {
        return new AutoCompleteParamReturnValue(AutoCompleteReturnValueFailure.parseFailure(failure));
    }


    public static class AutoCompleteParamReturnValueSuccess extends SuccessImpl {
        private final ShellSuggestion suggestion;

        private AutoCompleteParamReturnValueSuccess(ShellSuggestion suggestion) {
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
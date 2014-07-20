package com.rawcod.jerminal.returnvalue.autocomplete.entry;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValue.AutoCompleteEntryReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:46
 */
public class AutoCompleteEntryReturnValue extends ReturnValueImpl<AutoCompleteEntryReturnValueSuccess, AutoCompleteReturnValueFailure> {
    private AutoCompleteEntryReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static AutoCompleteEntryReturnValue success(ShellSuggestion suggestion) {
        return new AutoCompleteEntryReturnValue(new AutoCompleteEntryReturnValueSuccess(suggestion));
    }

    public static AutoCompleteEntryReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompleteEntryReturnValue(failure);
    }

    public static AutoCompleteEntryReturnValue parseFailure(ParseReturnValueFailure failure) {
        return new AutoCompleteEntryReturnValue(AutoCompleteReturnValueFailure.parseFailure(failure));
    }


    public static class AutoCompleteEntryReturnValueSuccess extends SuccessImpl {
        private final ShellSuggestion suggestion;

        private AutoCompleteEntryReturnValueSuccess(ShellSuggestion suggestion) {
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
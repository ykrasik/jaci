package com.rawcod.jerminal.returnvalue.autocomplete.path;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.path.AutoCompletePathReturnValue.AutoCompletePathReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 20:24
 */
public class AutoCompletePathReturnValue extends ReturnValueImpl<AutoCompletePathReturnValueSuccess, AutoCompleteReturnValueFailure> {
    private AutoCompletePathReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static AutoCompletePathReturnValue success(List<ShellDirectory> path, ShellSuggestion suggestion) {
        return new AutoCompletePathReturnValue(new AutoCompletePathReturnValueSuccess(path, suggestion));
    }

    public static AutoCompletePathReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompletePathReturnValue(failure);
    }

    public static AutoCompletePathReturnValue parseFailure(ParseReturnValueFailure failure) {
        return new AutoCompletePathReturnValue(AutoCompleteReturnValueFailure.parseFailure(failure));
    }


    public static class AutoCompletePathReturnValueSuccess extends SuccessImpl {
        private final List<ShellDirectory> path;
        private final ShellSuggestion suggestion;

        private AutoCompletePathReturnValueSuccess(List<ShellDirectory> path, ShellSuggestion suggestion) {
            this.path = checkNotNull(path, "path is null!");
            this.suggestion = checkNotNull(suggestion, "suggestion is null!");
        }

        public List<ShellDirectory> getPath() {
            return path;
        }

        public ShellSuggestion getSuggestion() {
            return suggestion;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("path", path)
                .add("suggestion", suggestion)
                .toString();
        }
    }
}

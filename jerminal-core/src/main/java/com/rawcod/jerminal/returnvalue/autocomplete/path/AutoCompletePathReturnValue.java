package com.rawcod.jerminal.returnvalue.autocomplete.path;

import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValueFailure;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.entry.AutoCompleteEntryReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 20:24
 */
public class AutoCompletePathReturnValue extends ReturnValueImpl<AutoCompletePathReturnValueSuccess, AutoCompletePathReturnValueFailure> {
    AutoCompletePathReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static AutoCompletePathReturnValue success(List<ShellDirectory> path, ShellSuggestion suggestion) {
        final AutoCompletePathReturnValueSuccess success = new AutoCompletePathReturnValueSuccess(path, suggestion);
        return new AutoCompletePathReturnValue(success);
    }

    public static AutoCompletePathReturnValueFailure.Builder failureBuilder(AutoCompleteError error) {
        return new AutoCompletePathReturnValueFailure.Builder(error);
    }

    public static AutoCompletePathReturnValue failureFrom(AutoCompleteEntryReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .build();
    }

    public static AutoCompletePathReturnValue failureFrom(ParsePathReturnValueFailure failure) {
        final ParseError parseError = failure.getError();
        final AutoCompleteError autoCompleteError = AutoCompleteError.translateParseError(parseError);
        return failureBuilder(autoCompleteError)
            .setMessage(failure.getMessage())
            .build();
    }

    public static AutoCompletePathReturnValue failureFrom(ParseEntryReturnValueFailure failure) {
        final ParseError parseError = failure.getError();
        final AutoCompleteError autoCompleteError = AutoCompleteError.translateParseError(parseError);
        return failureBuilder(autoCompleteError)
            .setMessage(failure.getMessage())
            .build();
    }
}

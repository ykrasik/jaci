package com.rawcod.jerminal.returnvalue.autocomplete.entry;

import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:46
 */
public class AutoCompleteEntryReturnValue extends ReturnValueImpl<AutoCompleteEntryReturnValueSuccess, AutoCompleteEntryReturnValueFailure> {
    AutoCompleteEntryReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static AutoCompleteEntryReturnValue success(ShellSuggestion suggestion) {
        final AutoCompleteEntryReturnValueSuccess success = new AutoCompleteEntryReturnValueSuccess(suggestion);
        return new AutoCompleteEntryReturnValue(success);
    }

    public static AutoCompleteEntryReturnValueFailure.Builder failureBuilder(AutoCompleteError error) {
        return new AutoCompleteEntryReturnValueFailure.Builder(error);
    }
}
package com.rawcod.jerminal.returnvalue.autocomplete.entry;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 21:46
 */
public class AutoCompleteEntryReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final ShellSuggestion suggestion;

    public AutoCompleteEntryReturnValueSuccess(ShellSuggestion suggestion) {
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


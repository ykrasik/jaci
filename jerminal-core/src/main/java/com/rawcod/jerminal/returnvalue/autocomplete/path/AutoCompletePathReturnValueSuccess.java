package com.rawcod.jerminal.returnvalue.autocomplete.path;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 20:24
 */
public class AutoCompletePathReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final List<ShellDirectory> path;
    private final ShellSuggestion suggestion;

    public AutoCompletePathReturnValueSuccess(List<ShellDirectory> path, ShellSuggestion suggestion) {
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

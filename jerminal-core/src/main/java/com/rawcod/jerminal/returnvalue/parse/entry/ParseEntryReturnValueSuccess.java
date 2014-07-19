package com.rawcod.jerminal.returnvalue.parse.entry;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:51
 */
public class ParseEntryReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final ShellEntry entry;

    ParseEntryReturnValueSuccess(ShellEntry entry) {
        this.entry = checkNotNull(entry, "Entry is null!");
    }

    public ShellEntry getEntry() {
        return entry;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("entry", entry)
            .toString();
    }
}

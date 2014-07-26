package com.rawcod.jerminal.returnvalue.parse.entry;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue.ParseEntryReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:51
 */
public class ParseEntryReturnValue extends ReturnValueImpl<ParseEntryReturnValueSuccess, ParseReturnValueFailure> {
    private ParseEntryReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseEntryReturnValue success(ShellEntry entry) {
        return new ParseEntryReturnValue(new ParseEntryReturnValueSuccess(entry));
    }

    public static ParseEntryReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseEntryReturnValue(failure);
    }


    public static class ParseEntryReturnValueSuccess extends SuccessImpl {
        private final ShellEntry entry;

        private ParseEntryReturnValueSuccess(ShellEntry entry) {
            this.entry = checkNotNull(entry, "entry is null!");
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
}

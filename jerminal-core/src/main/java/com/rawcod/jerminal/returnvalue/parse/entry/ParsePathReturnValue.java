package com.rawcod.jerminal.returnvalue.parse.entry;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue.ParsePathReturnValueSuccess;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:51
 */
public class ParsePathReturnValue extends ReturnValueImpl<ParsePathReturnValueSuccess, ParseReturnValueFailure> {
    private ParsePathReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParsePathReturnValue success(List<ShellDirectory> path, ShellEntry lastEntry) {
        return new ParsePathReturnValue(new ParsePathReturnValueSuccess(path, lastEntry));
    }

    public static ParsePathReturnValue failure(ParseReturnValueFailure failure) {
        return new ParsePathReturnValue(failure);
    }


    public static class ParsePathReturnValueSuccess extends SuccessImpl {
        private final List<ShellDirectory> path;
        private final ShellEntry lastEntry;

        private ParsePathReturnValueSuccess(List<ShellDirectory> path, ShellEntry lastEntry) {
            this.path = checkNotNull(path, "path is null!");
            this.lastEntry = checkNotNull(lastEntry, "lastEntry is null!");
        }

        public List<ShellDirectory> getPath() {
            return path;
        }

        public ShellEntry getLastEntry() {
            return lastEntry;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("path", path)
                .add("lastEntry", lastEntry)
                .toString();
        }
    }
}

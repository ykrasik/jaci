package com.rawcod.jerminal.returnvalue.parse.entry;

import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:51
 */
public class ParseEntryReturnValue extends ReturnValueImpl<ParseEntryReturnValueSuccess, ParseEntryReturnValueFailure> {
    ParseEntryReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ParseEntryReturnValue success(ShellEntry entry) {
        final ParseEntryReturnValueSuccess success = new ParseEntryReturnValueSuccess(entry);
        return new ParseEntryReturnValue(success);
    }

    public static ParseEntryReturnValueFailure.Builder failureBuilder(ParseError error) {
        return new ParseEntryReturnValueFailure.Builder(error);
    }
}

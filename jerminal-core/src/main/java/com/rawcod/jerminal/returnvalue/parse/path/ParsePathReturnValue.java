package com.rawcod.jerminal.returnvalue.parse.path;

import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValueFailure;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:51
 */
public class ParsePathReturnValue extends ReturnValueImpl<ParsePathReturnValueSuccess, ParsePathReturnValueFailure> {
    ParsePathReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ParsePathReturnValue success(List<ShellDirectory> path, ShellEntry entry) {
        final ParsePathReturnValueSuccess success = new ParsePathReturnValueSuccess(path, entry);
        return new ParsePathReturnValue(success);
    }

    public static ParsePathReturnValueFailure.Builder failureBuilder(ParseError error) {
        return new ParsePathReturnValueFailure.Builder(error);
    }

    public static ParsePathReturnValue failureFrom(ParseEntryReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }
}

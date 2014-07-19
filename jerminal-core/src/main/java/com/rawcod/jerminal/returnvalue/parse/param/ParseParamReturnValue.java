package com.rawcod.jerminal.returnvalue.parse.param;

import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseParamReturnValue extends ReturnValueImpl<ParseParamReturnValueSuccess, ParseParamReturnValueFailure> {
    ParseParamReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ParseParamReturnValue success(ShellEntry entry) {
        final ParseParamReturnValueSuccess success = new ParseParamReturnValueSuccess(entry, name);
        return new ParseParamReturnValue(success);
    }

    public static ParseParamReturnValueFailure.Builder failureBuilder(ParseError error) {
        return new ParseParamReturnValueFailure.Builder(error);
    }
}

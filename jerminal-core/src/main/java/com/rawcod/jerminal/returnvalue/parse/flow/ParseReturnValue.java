package com.rawcod.jerminal.returnvalue.parse.flow;

import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValueFailure;

import java.util.List;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ParseReturnValue extends ReturnValueImpl<ParseReturnValueSuccess, ParseReturnValueFailure> {
    ParseReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ParseReturnValue success(List<ShellDirectory> path,
                                           ShellCommand command,
                                           CommandArgs args) {
        final ParseReturnValueSuccess success = new ParseReturnValueSuccess(path, command, args);
        return new ParseReturnValue(success);
    }

    public static ParseReturnValueFailure.Builder failureBuilder(ParseError error) {
        return new ParseReturnValueFailure.Builder(error);
    }

    public static ParseReturnValue failureFrom(ParseEntryReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }

    public static ParseReturnValue failureFrom(ParsePathReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }

    public static ParseReturnValue failureFrom(ParseCommandArgsReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }
}
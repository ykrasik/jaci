package com.rawcod.jerminal.returnvalue.parse.args;

import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValueFailure;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseCommandArgsReturnValue extends ReturnValueImpl<ParseCommandArgsReturnValueSuccess, ParseCommandArgsReturnValueFailure> {
    ParseCommandArgsReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ParseCommandArgsReturnValue success(CommandArgs args) {
        final ParseCommandArgsReturnValueSuccess success = new ParseCommandArgsReturnValueSuccess(args);
        return new ParseCommandArgsReturnValue(success);
    }

    public static ParseCommandArgsReturnValueFailure.Builder failureBuilder(ParseError error) {
        return new ParseCommandArgsReturnValueFailure.Builder(error);
    }

    public static ParseCommandArgsReturnValue failureFrom(ParseParamReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }

    public static ParseCommandArgsReturnValue failureFrom(ParsePartialCommandArgsReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }
}

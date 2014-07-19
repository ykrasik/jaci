package com.rawcod.jerminal.returnvalue.parse.argspartial;

import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValueFailure;

import java.util.Map;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParsePartialCommandArgsReturnValue extends ReturnValueImpl<ParsePartialCommandArgsReturnValueSuccess, ParsePartialCommandArgsReturnValueFailure> {
    ParsePartialCommandArgsReturnValue(Failable returnValue) {
        super(returnValue);
    }

    public static ParsePartialCommandArgsReturnValue success(Map<String, Object> parsedArgs, Map<String, ShellParam> unboundParams) {
        final ParsePartialCommandArgsReturnValueSuccess success = new ParsePartialCommandArgsReturnValueSuccess(parsedArgs, unboundParams);
        return new ParsePartialCommandArgsReturnValue(success);
    }

    public static ParsePartialCommandArgsReturnValueFailure.Builder failureBuilder(ParseError error) {
        return new ParsePartialCommandArgsReturnValueFailure.Builder(error);
    }

    public static ParsePartialCommandArgsReturnValue failureFrom(ParseParamReturnValueFailure failure) {
        return failureBuilder(failure.getError())
            .setMessage(failure.getMessage())
            .setSuggestion(failure.getSuggestion())
            .build();
    }
}

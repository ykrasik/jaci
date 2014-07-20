package com.rawcod.jerminal.returnvalue.parse.argspartial;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.argspartial.ParsePartialCommandArgsReturnValue.ParsePartialCommandArgsReturnValueSuccess;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParsePartialCommandArgsReturnValue extends ReturnValueImpl<ParsePartialCommandArgsReturnValueSuccess, ParseReturnValueFailure> {
    private ParsePartialCommandArgsReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParsePartialCommandArgsReturnValue success(Map<String, Object> parsedArgs,
                                                             Map<String, ShellParam> unboundParams) {
        return new ParsePartialCommandArgsReturnValue(new ParsePartialCommandArgsReturnValueSuccess(parsedArgs, unboundParams));
    }

    public static ParsePartialCommandArgsReturnValue failure(ParseReturnValueFailure failure) {
        return new ParsePartialCommandArgsReturnValue(failure);
    }


    public static class ParsePartialCommandArgsReturnValueSuccess extends SuccessImpl {
        private final Map<String, Object> parsedArgs;
        private final Map<String, ShellParam> unboundParams;

        private ParsePartialCommandArgsReturnValueSuccess(Map<String, Object> parsedArgs,
                                                          Map<String, ShellParam> unboundParams) {
            this.parsedArgs = checkNotNull(parsedArgs, "parsedArgs is null!");
            this.unboundParams = checkNotNull(unboundParams, "unboundParams is null!");
        }

        public Map<String, Object> getParsedArgs() {
            return parsedArgs;
        }

        public Map<String, ShellParam> getUnboundParams() {
            return unboundParams;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("unboundParams", unboundParams)
                .add("parsedArgs", parsedArgs)
                .toString();
        }
    }
}

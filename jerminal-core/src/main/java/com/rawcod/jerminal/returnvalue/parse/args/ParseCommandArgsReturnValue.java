package com.rawcod.jerminal.returnvalue.parse.args;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue.ParseCommandArgsReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseCommandArgsReturnValue extends ReturnValueImpl<ParseCommandArgsReturnValueSuccess, ParseReturnValueFailure> {
    private ParseCommandArgsReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseCommandArgsReturnValue success(CommandArgs args) {
        return new ParseCommandArgsReturnValue(new ParseCommandArgsReturnValueSuccess(args));
    }

    public static ParseCommandArgsReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseCommandArgsReturnValue(failure);
    }


    public static class ParseCommandArgsReturnValueSuccess extends SuccessImpl {
        private final CommandArgs args;

        private ParseCommandArgsReturnValueSuccess(CommandArgs args) {
            this.args = checkNotNull(args, "args");
        }

        public CommandArgs getArgs() {
            return args;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("args", args)
                .toString();
        }
    }
}

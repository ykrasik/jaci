package com.rawcod.jerminal.returnvalue.parse.flow;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue.ParseReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ParseReturnValue extends ReturnValueImpl<ParseReturnValueSuccess, ParseReturnValueFailure> {
    private ParseReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseReturnValue success(ShellCommand command, CommandArgs args) {
        return new ParseReturnValue(new ParseReturnValueSuccess(command, args));
    }

    public static ParseReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseReturnValue(failure);
    }


    public static class ParseReturnValueSuccess extends SuccessImpl {
        private final ShellCommand command;
        private final CommandArgs args;

        private ParseReturnValueSuccess(ShellCommand command, CommandArgs args) {
            this.command = checkNotNull(command, "command");
            this.args = checkNotNull(args, "args");
        }

        public ShellCommand getCommand() {
            return command;
        }

        public CommandArgs getArgs() {
            return args;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("command", command)
                .add("args", args)
                .toString();
        }
    }
}
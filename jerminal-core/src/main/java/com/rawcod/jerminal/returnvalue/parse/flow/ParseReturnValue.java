package com.rawcod.jerminal.returnvalue.parse.flow;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue.ParseReturnValueSuccess;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ParseReturnValue extends ReturnValueImpl<ParseReturnValueSuccess, ParseReturnValueFailure> {
    private ParseReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseReturnValue success(List<ShellDirectory> path,
                                           ShellCommand command,
                                           CommandArgs args) {
        return new ParseReturnValue(new ParseReturnValueSuccess(path, command, args));
    }

    public static ParseReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseReturnValue(failure);
    }


    public static class ParseReturnValueSuccess extends SuccessImpl {
        private final List<ShellDirectory> path;
        private final ShellCommand command;
        private final CommandArgs args;

        private ParseReturnValueSuccess(List<ShellDirectory> path,
                                        ShellCommand command,
                                        CommandArgs args) {
            this.path = Collections.unmodifiableList(checkNotNull(path, "path is null!"));
            this.command = checkNotNull(command, "command is null!");
            this.args = checkNotNull(args, "args is null!");
        }

        public List<ShellDirectory> getPath() {
            return path;
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
                .add("path", path)
                .add("command", command)
                .add("args", args)
                .toString();
        }
    }
}
package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ParseReturnValue {
    private final ShellCommand command;
    private final CommandArgs args;

    public ParseReturnValue(ShellCommand command, CommandArgs args) {
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
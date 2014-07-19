package com.rawcod.jerminal.returnvalue.parse.flow;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 17/07/2014
 * Time: 10:18
 */
public class ParseReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final List<ShellDirectory> path;
    private final ShellCommand command;
    private final CommandArgs args;

    public ParseReturnValueSuccess(List<ShellDirectory> path,
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

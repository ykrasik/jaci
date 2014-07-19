package com.rawcod.jerminal.returnvalue.parse.args;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:08
 */
public class ParseCommandArgsReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final CommandArgs args;

    ParseCommandArgsReturnValueSuccess(CommandArgs args) {
        this.args = checkNotNull(args, "args is null!");
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

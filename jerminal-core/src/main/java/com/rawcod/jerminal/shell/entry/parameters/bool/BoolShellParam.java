package com.rawcod.jerminal.shell.entry.parameters.bool;

import com.rawcod.jerminal.shell.entry.parameters.string.StringShellParam;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class BoolShellParam extends StringShellParam {
    public BoolShellParam(String name) {
        super(name, "true", "false");
    }

    @Override
    public ShellParseReturnValue<Boolean> parse(String arg) {
        final ShellParseReturnValue<?> returnValue = super.parse(arg);
        if (!returnValue.isSuccess()) {
            return ShellParseReturnValue.failureFrom(returnValue);
        }

        final String parsedEntry = (String) returnValue.getParsedValue();
        return ShellParseReturnValue.success(Boolean.parseBoolean(parsedEntry));
    }

    @Override
    public String toString() {
        return String.format("{%s: bool}", name);
    }
}

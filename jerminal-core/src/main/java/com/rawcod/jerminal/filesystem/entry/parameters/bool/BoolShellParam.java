package com.rawcod.jerminal.filesystem.entry.parameters.bool;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.filesystem.entry.parameters.string.StringShellParam;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class BoolShellParam extends StringShellParam {
    public BoolShellParam(String name) {
        super(name, "true", "false");
    }

    @Override
    public ParseReturnValue<Boolean> parse(String arg) {
        final ParseReturnValue<?> returnValue = super.parse(arg);
        if (!returnValue.isSuccess()) {
            return ParseReturnValue.failureFrom(returnValue);
        }

        final String parsedEntry = (String) returnValue.getParsedValue();
        return ParseReturnValue.success(Boolean.parseBoolean(parsedEntry));
    }

    @Override
    public String toString() {
        return String.format("{%s: bool}", name);
    }
}

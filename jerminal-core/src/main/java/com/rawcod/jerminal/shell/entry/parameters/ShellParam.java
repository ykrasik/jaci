package com.rawcod.jerminal.shell.entry.parameters;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public interface ShellParam {
    void install(ShellManager manager);

    ShellAutoCompleteReturnValue autoComplete(String arg);

    ShellParseReturnValue<?> parse(String arg);
}

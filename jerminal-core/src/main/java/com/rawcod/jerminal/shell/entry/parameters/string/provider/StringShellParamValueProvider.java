package com.rawcod.jerminal.shell.entry.parameters.string.provider;

import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 25/01/14
 */
public interface StringShellParamValueProvider {
    ShellAutoCompleteReturnValue autoComplete(String arg);

    ShellParseReturnValue<?> parse(String arg);

}

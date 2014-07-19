package com.rawcod.jerminal.filesystem.entry.parameters.string.provider;

import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;

/**
 * User: ykrasik
 * Date: 25/01/14
 */
public interface StringShellParamValueProvider {
    AutoCompleteReturnValue autoComplete(String arg);

    ParseReturnValue<?> parse(String arg);

}

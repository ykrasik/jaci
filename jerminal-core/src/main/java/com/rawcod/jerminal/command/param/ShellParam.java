package com.rawcod.jerminal.command.param;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public interface ShellParam {
    String getName();
    String getDescription();

    boolean isOptional();

    ParseParamValueReturnValue parse(Optional<String> rawValue, ParamParseContext context);
    AutoCompleteReturnValue autoComplete(Optional<String> rawValue, ParamParseContext context);
}

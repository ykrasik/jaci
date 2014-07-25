package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public interface CommandParam {
    String getName();
    String getDescription();

    boolean isOptional();

    ParseParamValueReturnValue parse(Optional<String> rawValue, ParamParseContext context);
    AutoCompleteReturnValue autoComplete(Optional<String> prefix, ParamParseContext context);
}

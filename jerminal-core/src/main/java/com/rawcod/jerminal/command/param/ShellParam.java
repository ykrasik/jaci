package com.rawcod.jerminal.command.param;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamValueReturnValue;
import com.rawcod.jerminal.returnvalue.parse.paramvalue.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public interface ShellParam {
    String getName();
    String getDescription();

    // FIXME: Not amazing... leaky abstraction.
    boolean isOptional();

    ParseParamValueReturnValue parse(Optional<String> rawValue, ParamParseContext context);
    AutoCompleteParamValueReturnValue autoComplete(Optional<String> rawValue, ParamParseContext context);

    Object getDefaultValue();
}

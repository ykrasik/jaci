package com.rawcod.jerminal.command.param;

import com.rawcod.jerminal.returnvalue.autocomplete.param.AutoCompleteParamValueReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 16:24
 */
public interface ShellParamParser {
    String getName();
    String getDescription();

    AutoCompleteParamValueReturnValue autoComplete(String arg, ParamParseContext context);
    ParseParamReturnValue parse(String arg, ParamParseContext context);
}

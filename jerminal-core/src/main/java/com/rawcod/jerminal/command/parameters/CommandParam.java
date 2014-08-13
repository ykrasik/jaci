package com.rawcod.jerminal.command.parameters;

import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public interface CommandParam {
    String getName();
    String getDescription();

    ParamType getType();
    String getExternalForm();

    Object parse(String rawValue, ParseParamContext context) throws ParseException;
    Object unbound(ParseParamContext context) throws ParseException;
    AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) throws ParseException;
}

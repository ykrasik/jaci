package com.rawcod.jerminal.command.parameters.number;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 21:33
 */
public abstract class AbstractNumberCommandParam<T> extends AbstractMandatoryCommandParam<T> {
    protected AbstractNumberCommandParam(String name, String description) {
        super(name, description);
    }

    @Override
    public T parse(String rawValue, ParseParamContext context) throws ParseException {
        try {
            return parseNumber(rawValue);
        } catch (NumberFormatException ignored) {
            throw ParseErrors.invalidParamValue(getExternalForm(), rawValue);
        }
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) throws ParseException {
        // Numbers cannot be auto-completed.
        throw ParseErrors.invalidParamValue(getExternalForm(), prefix);
    }

    protected abstract T parseNumber(String rawValue);
}

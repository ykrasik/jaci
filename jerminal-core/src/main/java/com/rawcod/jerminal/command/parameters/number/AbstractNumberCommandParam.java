package com.rawcod.jerminal.command.parameters.number;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 21:33
 */
public abstract class AbstractNumberCommandParam<T> extends AbstractMandatoryCommandParam {
    protected AbstractNumberCommandParam(String name, String description) {
        super(name, description);
    }

    @Override
    public ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        try {
            final T parsedValue = parseNumber(rawValue);
            return ParseParamValueReturnValue.success(parsedValue);
        } catch (NumberFormatException ignored) {
            return ParseErrors.invalidParamValue(getName(), rawValue);
        }
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) {
        // Integers cannot be auto-completed.
        return AutoCompleteErrors.noPossibleValuesForNumberParam(getName());
    }

    protected abstract T parseNumber(String rawValue);
}

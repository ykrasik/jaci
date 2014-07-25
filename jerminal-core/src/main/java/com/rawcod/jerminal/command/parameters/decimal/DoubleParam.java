package com.rawcod.jerminal.command.parameters.decimal;

import com.rawcod.jerminal.command.parameters.AbstractCommandParam;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class DoubleParam extends AbstractCommandParam {
    public DoubleParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParamParseContext context) {
        try {
            final Double parsedValue = Double.parseDouble(rawValue);
            return ParseParamValueReturnValue.success(parsedValue);
        } catch (NumberFormatException ignored) {
            return ParseParamValueReturnValue.failure(ParseReturnValueFailure.InvalidParamValue(getName(), rawValue));
        }
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParamParseContext context) {
        // Doubles cannot be auto-completed.
        return AutoCompleteReturnValue.failure(
            AutoCompleteReturnValueFailure.from(
                AutoCompleteError.NO_POSSIBLE_VALUES,
                "AutoComplete error: Double parameters cannot be auto completed: '%s'", getName()
            )
        );
    }

    @Override
    public String toString() {
        return String.format("{%s: double}", getName());
    }
}

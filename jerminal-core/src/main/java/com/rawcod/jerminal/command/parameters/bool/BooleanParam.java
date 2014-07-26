package com.rawcod.jerminal.command.parameters.bool;

import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.command.parameters.string.StringParam;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue.ParseParamValueReturnValueSuccess;

import java.util.Arrays;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class BooleanParam extends StringParam {
    public BooleanParam(String name, String description) {
        super(name, description, Arrays.asList("true", "false"));
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParamParseContext context) {
        final ParseParamValueReturnValue returnValue = super.parse(rawValue, context);
        if (returnValue.isFailure()) {
            return returnValue;
        }

        // If parsing was successful, the value is either "true" or "false".
        final ParseParamValueReturnValueSuccess success = returnValue.getSuccess();
        final Boolean boolValue = Boolean.parseBoolean(success.getValue().toString());
        return ParseParamValueReturnValue.success(boolValue);
    }

    @Override
    public String toString() {
        return String.format("{%s: bool}", getName());
    }
}

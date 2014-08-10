package com.rawcod.jerminal.command.parameters.bool;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.command.parameters.string.StringParam;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue.ParseParamValueReturnValueSuccess;

import java.util.Arrays;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class BooleanParam extends StringParam {
    private static final Supplier<Trie<String>> VALUES_SUPPLIER = Params.constStringValuesSupplier(Arrays.asList("true", "false"));

    public BooleanParam(String name, String description) {
        super(name, description, VALUES_SUPPLIER);
    }

    @Override
    protected String getExternalFormType() {
        return "bool";
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        final ParseParamValueReturnValue returnValue = super.parse(rawValue, context);
        if (returnValue.isFailure()) {
            return returnValue;
        }

        // If parsing was successful, the value is either "true" or "false".
        final ParseParamValueReturnValueSuccess success = returnValue.getSuccess();
        final Boolean boolValue = Boolean.parseBoolean(success.getValue().toString());
        return ParseParamValueReturnValue.success(boolValue);
    }
}

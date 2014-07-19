package com.rawcod.jerminal.returnvalue.parse.param;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:08
 */
public class ParseParamReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final String paramName;
    private final Object value;

    ParseParamReturnValueSuccess(Object value, String paramName) {
        this.paramName = checkNotNull(paramName, "paramName is null!");
        this.value = checkNotNull(value, "value is null!");
    }

    public String getParamName() {
        return paramName;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("paramName", paramName)
            .add("value", value)
            .toString();
    }
}

package com.rawcod.jerminal.returnvalue.parse.param;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue.ParseParamReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseParamReturnValue extends ReturnValueImpl<ParseParamReturnValueSuccess, ParseReturnValueFailure> {
    private ParseParamReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseParamReturnValue success(String paramName, Object value) {
        return new ParseParamReturnValue(new ParseParamReturnValueSuccess(paramName, value));
    }

    public static ParseParamReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseParamReturnValue(failure);
    }


    public static class ParseParamReturnValueSuccess extends SuccessImpl {
        private final String paramName;
        private final Object value;

        private ParseParamReturnValueSuccess(String paramName, Object value) {
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
}

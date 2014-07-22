package com.rawcod.jerminal.returnvalue.parse.param;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue.ParseParamValueReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseParamValueReturnValue extends ReturnValueImpl<ParseParamValueReturnValueSuccess, ParseReturnValueFailure> {
    private ParseParamValueReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseParamValueReturnValue success(Object value) {
        return new ParseParamValueReturnValue(new ParseParamValueReturnValueSuccess(value));
    }

    public static ParseParamValueReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseParamValueReturnValue(failure);
    }


    public static class ParseParamValueReturnValueSuccess extends SuccessImpl {
        private final Object value;

        private ParseParamValueReturnValueSuccess(Object value) {
            this.value = checkNotNull(value, "value is null!");
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("value", value)
                .toString();
        }
    }
}

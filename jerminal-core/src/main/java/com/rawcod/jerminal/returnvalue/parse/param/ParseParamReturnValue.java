package com.rawcod.jerminal.returnvalue.parse.param;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
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


    public static ParseParamReturnValue success(CommandParam param, Object value) {
        return new ParseParamReturnValue(new ParseParamReturnValueSuccess(param, value));
    }

    public static ParseParamReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseParamReturnValue(failure);
    }


    public static class ParseParamReturnValueSuccess extends SuccessImpl {
        private final CommandParam param;
        private final Object value;

        private ParseParamReturnValueSuccess(CommandParam param, Object value) {
            this.param = checkNotNull(param, "param");
            this.value = checkNotNull(value, "value");
        }

        public CommandParam getParam() {
            return param;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("param", param)
                .add("value", value)
                .toString();
        }
    }
}

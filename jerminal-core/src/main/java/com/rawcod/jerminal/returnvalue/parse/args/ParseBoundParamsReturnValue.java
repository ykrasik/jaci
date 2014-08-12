package com.rawcod.jerminal.returnvalue.parse.args;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.args.ParseBoundParamsReturnValue.ParseBoundParamsReturnValueSuccess;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseBoundParamsReturnValue extends ReturnValueImpl<ParseBoundParamsReturnValueSuccess, ParseReturnValueFailure> {
    private ParseBoundParamsReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static ParseBoundParamsReturnValue success(Map<String, Object> parsedArgs,
                                                      List<CommandParam> unboundParams) {
        return new ParseBoundParamsReturnValue(new ParseBoundParamsReturnValueSuccess(parsedArgs, unboundParams));
    }

    public static ParseBoundParamsReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseBoundParamsReturnValue(failure);
    }


    public static class ParseBoundParamsReturnValueSuccess extends SuccessImpl {
        private final Map<String, Object> boundParams;
        private final List<CommandParam> unboundParams;

        private ParseBoundParamsReturnValueSuccess(Map<String, Object> boundParams,
                                                   List<CommandParam> unboundParams) {
            this.boundParams = checkNotNull(boundParams, "boundParams");
            this.unboundParams = checkNotNull(unboundParams, "unboundParams");
        }

        public Map<String, Object> getBoundParams() {
            return boundParams;
        }

        public List<CommandParam> getUnboundParams() {
            return unboundParams;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("boundParams", boundParams)
                .add("unboundParams", unboundParams)
                .toString();
        }
    }
}

package com.rawcod.jerminal.returnvalue.parse.args;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.OptionalCommandParam;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.args.ParseBoundParamsReturnValue.ParseBoundParamsReturnValueSuccess;

import java.util.Collection;
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
                                                      Collection<CommandParam> unboundMandatoryParams,
                                                      Collection<OptionalCommandParam> unboundOptionalParams) {
        return new ParseBoundParamsReturnValue(new ParseBoundParamsReturnValueSuccess(parsedArgs, unboundMandatoryParams, unboundOptionalParams));
    }

    public static ParseBoundParamsReturnValue failure(ParseReturnValueFailure failure) {
        return new ParseBoundParamsReturnValue(failure);
    }


    public static class ParseBoundParamsReturnValueSuccess extends SuccessImpl {
        private final Map<String, Object> parsedArgs;
        private final Collection<CommandParam> unboundMandatoryParams;
        private final Collection<OptionalCommandParam> unboundOptionalParams;

        private ParseBoundParamsReturnValueSuccess(Map<String, Object> parsedArgs,
                                                   Collection<CommandParam> unboundMandatoryParams,
                                                   Collection<OptionalCommandParam> unboundOptionalParams) {
            this.parsedArgs = checkNotNull(parsedArgs, "parsedArgs is null!");
            this.unboundMandatoryParams = checkNotNull(unboundMandatoryParams, "unboundMandatoryParams is null!");
            this.unboundOptionalParams = checkNotNull(unboundOptionalParams, "unboundOptionalParams is null!");
        }

        public Map<String, Object> getParsedArgs() {
            return parsedArgs;
        }

        public Collection<CommandParam> getUnboundMandatoryParams() {
            return unboundMandatoryParams;
        }

        public Collection<OptionalCommandParam> getUnboundOptionalParams() {
            return unboundOptionalParams;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("parsedArgs", parsedArgs)
                .add("unboundMandatoryParams", unboundMandatoryParams)
                .add("unboundOptionalParams", unboundOptionalParams)
                .toString();
        }
    }
}

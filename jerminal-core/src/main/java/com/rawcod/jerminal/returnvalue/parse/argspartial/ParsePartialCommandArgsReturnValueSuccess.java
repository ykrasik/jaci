package com.rawcod.jerminal.returnvalue.parse.argspartial;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:08
 */
public class ParsePartialCommandArgsReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final Map<String, Object> parsedArgs;
    private final Map<String, ShellParam> unboundParams;

    ParsePartialCommandArgsReturnValueSuccess(Map<String, Object> parsedArgs, Map<String, ShellParam> unboundParams) {
        this.parsedArgs = checkNotNull(parsedArgs, "parsedArgs is null!");
        this.unboundParams = checkNotNull(unboundParams, "unboundParams is null!");
    }

    public Map<String, Object> getParsedArgs() {
        return parsedArgs;
    }

    public Map<String, ShellParam> getUnboundParams() {
        return unboundParams;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("unboundParams", unboundParams)
            .add("parsedArgs", parsedArgs)
            .toString();
    }
}

package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.parameters.CommandParam;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParseBoundParamsReturnValue {
    private final Map<String, Object> boundParams;
    private final List<CommandParam> unboundParams;

    public ParseBoundParamsReturnValue(Map<String, Object> boundParams, List<CommandParam> unboundParams) {
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

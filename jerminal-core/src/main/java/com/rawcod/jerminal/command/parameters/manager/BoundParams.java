package com.rawcod.jerminal.command.parameters.manager;

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
public class BoundParams {
    private final List<CommandParam> unboundParams;
    private final Map<String, Object> boundParams;

    public BoundParams(List<CommandParam> unboundParams,
                       Map<String, Object> boundParams) {
        this.unboundParams = checkNotNull(unboundParams, "unboundParams");
        this.boundParams = checkNotNull(boundParams, "boundParams");
    }

    public List<CommandParam> getUnboundParams() {
        return unboundParams;
    }

    public Map<String, Object> getBoundParams() {
        return boundParams;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("unboundParams", unboundParams)
            .add("boundParams", boundParams)
            .toString();
    }
}

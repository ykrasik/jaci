package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.rawcod.jerminal.command.parameters.CommandParam;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:06
 */
public class ParsedParam {
    private final CommandParam param;
    private final Object value;

    public ParsedParam(CommandParam param, Object value) {
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

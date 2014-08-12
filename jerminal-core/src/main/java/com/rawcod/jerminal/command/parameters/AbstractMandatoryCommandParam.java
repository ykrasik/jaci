package com.rawcod.jerminal.command.parameters;

import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 16:19
 */
public abstract class AbstractMandatoryCommandParam implements CommandParam {
    private final String name;
    private final String description;

    protected AbstractMandatoryCommandParam(String name, String description) {
        this.name = checkNotNull(name, "name");
        this.description = checkNotNull(description, "description");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ParamType getType() {
        return ParamType.MANDATORY;
    }

    @Override
    public ParseParamValueReturnValue unbound(ParseParamContext context) {
        return ParseErrors.paramNotBound(name);
    }

    @Override
    public String getExternalForm() {
        final String type = getExternalFormType();
        return String.format("{%s: %s}", name, type);
    }

    protected abstract String getExternalFormType();

    @Override
    public String toString() {
        return getExternalForm();
    }
}

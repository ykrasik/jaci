package com.rawcod.jerminal.command.parameters.flag;

import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamType;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 21:46
 */
public class FlagParam implements CommandParam {
    private final String name;
    private final String description;

    public FlagParam(String name, String description) {
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
        return ParamType.FLAG;
    }

    @Override
    public String getExternalForm() {
        return String.format("[%s: flag]", name);
    }

    @Override
    public ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        return ParseErrors.invalidFlagValue(getName());
    }

    @Override
    public ParseParamValueReturnValue unbound(ParseParamContext context) {
        return ParseParamValueReturnValue.success(false);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) {
        return AutoCompleteErrors.invalidFlagValue(getName());
    }

    @Override
    public String toString() {
        return getExternalForm();
    }
}

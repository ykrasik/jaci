package com.rawcod.jerminal.command.parameters.flag;

import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamType;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

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
    public Boolean parse(String rawValue, ParseParamContext context) throws ParseException {
        throw ParseErrors.invalidFlagValue(getName());
    }

    @Override
    public Boolean unbound(ParseParamContext context) throws ParseException {
        return false;
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) throws ParseException {
        throw ParseErrors.invalidFlagValue(getName());
    }

    @Override
    public String toString() {
        return getExternalForm();
    }
}

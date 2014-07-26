package com.rawcod.jerminal.command.parameters.flag;

import com.google.common.base.Optional;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.command.parameters.ParamType;
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
        this.name = checkNotNull(name, "name is null!");
        this.description = checkNotNull(description, "description is null!");
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
    public ParseParamValueReturnValue parse(Optional<String> rawValue, ParamParseContext context) {
        // Flags are not allowed to have a value.
        // Their presence is enough to set the flag to true.
        if (rawValue.isPresent()) {
            return ParseErrors.invalidFlagValue(getName());
        }
        return ParseParamValueReturnValue.success(true);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(Optional<String> prefix, ParamParseContext context) {
        return AutoCompleteErrors.invalidFlagValue(getName());
    }

    @Override
    public String toString() {
        return String.format("[%s: flag]", name);
    }
}

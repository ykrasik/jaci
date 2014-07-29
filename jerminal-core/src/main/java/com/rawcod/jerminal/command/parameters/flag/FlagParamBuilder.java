package com.rawcod.jerminal.command.parameters.flag;

import com.rawcod.jerminal.command.parameters.CommandParam;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class FlagParamBuilder {
    private final String name;
    private String description = "flag";

    public FlagParamBuilder(String name) {
        this.name = name;
    }

    public CommandParam build() {
        return new FlagParam(name, description);
    }

    public FlagParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
}

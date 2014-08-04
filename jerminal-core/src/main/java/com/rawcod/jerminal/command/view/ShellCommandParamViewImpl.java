package com.rawcod.jerminal.command.view;

import com.rawcod.jerminal.command.parameters.ParamType;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 20:49
 */
public class ShellCommandParamViewImpl implements ShellCommandParamView {
    private final String name;
    private final String description;
    private final ParamType type;
    private final String externalForm;

    public ShellCommandParamViewImpl(String name, String description, ParamType type, String externalForm) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.externalForm = externalForm;
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
        return type;
    }

    @Override
    public String getExternalForm() {
        return externalForm;
    }

    @Override
    public String toString() {
        return externalForm;
    }
}

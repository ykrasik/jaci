package com.rawcod.jerminal.command.view;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 20:48
 */
public class ShellCommandViewImpl implements ShellCommandView {
    private final String name;
    private final String description;
    private final List<ShellCommandParamView> params;

    public ShellCommandViewImpl(String name, String description, List<ShellCommandParamView> params) {
        this.name = name;
        this.description = description;
        this.params = Collections.unmodifiableList(params);
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
    public List<ShellCommandParamView> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return name;
    }
}

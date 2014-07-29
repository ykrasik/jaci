package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.parameters.CommandParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:56
 */
public class ShellCommandBuilder {
    private final String name;
    private String description = "Command";
    private List<CommandParam> params = new ArrayList<>(4);
    private CommandExecutor executor;

    public ShellCommandBuilder(String name) {
        this.name = name;
    }

    public ShellCommand build() {
        return new ShellCommand(name, description, params, executor);
    }

    public ShellCommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ShellCommandBuilder addParam(CommandParam param) {
        this.params.add(param);
        return this;
    }

    public ShellCommandBuilder addParams(CommandParam... params) {
        return addParams(Arrays.asList(params));
    }

    public ShellCommandBuilder addParams(List<CommandParam> params) {
        this.params.addAll(params);
        return this;
    }

    public ShellCommandBuilder setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }
}

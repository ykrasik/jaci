package com.rawcod.jerminal.command.parameters.entry;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class FileParamBuilder {
    private final String name;
    private final ShellFileSystem fileSystem;

    private String description = "file";
    private Supplier<ShellCommand> defaultValueSupplier;

    public FileParamBuilder(String name, ShellFileSystem fileSystem) {
        this.name = name;
        this.fileSystem = fileSystem;
    }

    public CommandParam build() {
        final CommandParam param = new FileParam(name, description, fileSystem);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public FileParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public FileParamBuilder setOptional(ShellCommand defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public FileParamBuilder setOptional(Supplier<ShellCommand> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}

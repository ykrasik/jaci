package com.rawcod.jerminal.command.parameters.entry;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class DirectoryParamBuilder {
    private final String name;
    private final ShellFileSystem fileSystem;

    private String description = "directory";
    private Supplier<ShellDirectory> defaultValueSupplier;

    public DirectoryParamBuilder(String name, ShellFileSystem fileSystem) {
        this.name = name;
        this.fileSystem = fileSystem;
    }

    public CommandParam build() {
        final CommandParam param = new DirectoryParam(name, description, fileSystem);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public DirectoryParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public DirectoryParamBuilder setOptional(ShellDirectory defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public DirectoryParamBuilder setOptional(Supplier<ShellDirectory> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}

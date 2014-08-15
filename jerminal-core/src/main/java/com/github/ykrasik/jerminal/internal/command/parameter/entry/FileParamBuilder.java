/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.command.parameter.entry;

import com.google.common.base.Supplier;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamUtils;
import com.github.ykrasik.jerminal.internal.command.parameter.optional.OptionalParam;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.api.command.ShellCommand;

/**
 * A builder for a {@link FileParam}.<br>
 * By default creates mandatory parameters, but can be set to create optional parameters via
 * {@link #setOptional(ShellCommand)} and {@link #setOptional(Supplier)}.<br>
 * Intended for internal use with control commands.
 *
 * @author Yevgeny Krasik
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
        return setOptional(ParamUtils.constValueSupplier(defaultValue));
    }

    public FileParamBuilder setOptional(Supplier<ShellCommand> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}

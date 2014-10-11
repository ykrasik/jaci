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

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamUtils;
import com.github.ykrasik.jerminal.internal.command.parameter.optional.OptionalParam;
import com.github.ykrasik.jerminal.internal.filesystem.InternalShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;
import com.google.common.base.Supplier;

import java.util.Objects;

/**
 * A builder for a {@link DirectoryParam}.<br>
 * By default creates mandatory parameters, but can be set to create optional parameters via
 * {@link #setOptional(ShellDirectory)} and {@link #setOptional(Supplier)}.<br>
 * Intended for internal use with control commands.
 *
 * @author Yevgeny Krasik
 */
public class DirectoryParamBuilder {
    private final String name;
    private final InternalShellFileSystem fileSystem;

    private String description = "directory";
    private Supplier<InternalShellDirectory> defaultValueSupplier;

    public DirectoryParamBuilder(String name, InternalShellFileSystem fileSystem) {
        this.name = Objects.requireNonNull(name);
        this.fileSystem = Objects.requireNonNull(fileSystem);
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

    public DirectoryParamBuilder setOptional(InternalShellDirectory defaultValue) {
        return setOptional(ParamUtils.constValueSupplier(defaultValue));
    }

    public DirectoryParamBuilder setOptional(Supplier<InternalShellDirectory> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}

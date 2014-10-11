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

import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.internal.command.parameter.AbstractMandatoryCommandParam;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.InternalShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;

import java.util.Objects;

/**
 * A {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam} that parses {@link InternalShellDirectory} values.<br>
 * Intended for internal use with control commands.
 *
 * @author Yevgeny Krasik
 */
public class DirectoryParam extends AbstractMandatoryCommandParam<InternalShellDirectory> {
    private final InternalShellFileSystem fileSystem;

    public DirectoryParam(String name, String description, InternalShellFileSystem fileSystem) {
        super(name, description);
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    @Override
    protected String getExternalFormType() {
        return "directory";
    }

    @Override
    public InternalShellDirectory parse(String rawValue) throws ParseException {
        if (rawValue.isEmpty()) {
            throw emptyValue();
        }
        return fileSystem.parsePathToDirectory(rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        return fileSystem.autoCompletePathToDirectory(prefix);
    }

    private ParseException emptyValue() {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "No directory was supplied!"
        );
    }
}

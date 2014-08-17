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

package com.github.ykrasik.jerminal.api.command;

import com.github.ykrasik.jerminal.api.exception.MissingParameterException;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;

import java.util.Map;

/**
 * Holds the args that were passed to the command.<br>
 * Arguments are always accessed by name.
 *
 * @author Yevgeny Krasik
 */
// TODO: Annotation based config.
public class CommandArgs {
    private final Map<String, Object> args;

    public CommandArgs(Map<String, Object> args) {
        this.args = args;
    }

    public String getString(String name) throws MissingParameterException {
        return getParam(name, String.class);
    }

    public int getInt(String name) throws MissingParameterException {
        return getParam(name, Integer.class);
    }

    public double getDouble(String name) throws MissingParameterException {
        return getParam(name, Double.class);
    }

    public boolean getBool(String name) throws MissingParameterException {
        return getParam(name, Boolean.class);
    }

    public ShellDirectory getDirectory(String name) throws MissingParameterException {
        return getParam(name, ShellDirectory.class);
    }

    public ShellCommand getFile(String name) throws MissingParameterException {
        return getParam(name, ShellCommand.class);
    }

    private <T> T getParam(String name, Class<T> clazz) throws MissingParameterException {
        final Object value = args.get(name);
        if (value == null) {
            throw new MissingParameterException("No value defined for param '%s'!", name);
        }

        return clazz.cast(value);
    }
}

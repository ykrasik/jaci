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
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFile;

import java.util.Map;
import java.util.Queue;

/**
 * Holds the args that were passed to the command.<br>
 * Arguments are always accessed by name.
 *
 * @author Yevgeny Krasik
 */
// TODO: Annotation based config.
public class CommandArgs {
    private final Map<String, Object> args;
    private final Queue<Object> positionalArgValues;

    public CommandArgs(Map<String, Object> args, Queue<Object> positionalArgValues) {
        this.args = args;
        this.positionalArgValues = positionalArgValues;
    }

    public String getString(String name) throws MissingParameterException {
        return getArg(name, String.class);
    }

    public String popString() throws MissingParameterException {
        return popArg(String.class);
    }

    public int getInt(String name) throws MissingParameterException {
        return getArg(name, Integer.class);
    }

    public int popInt() throws MissingParameterException {
        return popArg(Integer.class);
    }

    public double getDouble(String name) throws MissingParameterException {
        return getArg(name, Double.class);
    }

    public double popDouble() throws MissingParameterException {
        return popArg(Double.class);
    }

    public boolean getBool(String name) throws MissingParameterException {
        return getArg(name, Boolean.class);
    }

    public boolean popBool() throws MissingParameterException {
        return popArg(Boolean.class);
    }

    public ShellDirectory getDirectory(String name) throws MissingParameterException {
        return getArg(name, ShellDirectory.class);
    }

    public ShellDirectory popDirectory() throws MissingParameterException {
        return popArg(ShellDirectory.class);
    }

    public ShellFile getFile(String name) throws MissingParameterException {
        return getArg(name, ShellFile.class);
    }

    public ShellFile popFile() throws MissingParameterException {
        return popArg(ShellFile.class);
    }

    private <T> T getArg(String name, Class<T> clazz) throws MissingParameterException {
        final Object value = args.get(name);
        if (value == null) {
            throw new MissingParameterException("No value defined for param '%s'!", name);
        }

        return clazz.cast(value);
    }

    private <T> T popArg(Class<T> clazz) throws MissingParameterException {
        if (positionalArgValues.isEmpty()) {
            throw new MissingParameterException("No more arguments!");
        }

        final Object value = positionalArgValues.poll();
        return clazz.cast(value);
    }
}

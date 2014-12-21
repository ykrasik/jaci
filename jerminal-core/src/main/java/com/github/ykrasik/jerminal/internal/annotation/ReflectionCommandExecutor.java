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

package com.github.ykrasik.jerminal.internal.annotation;

import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.internal.command.PrivilegedCommandArgs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * A {@link CommandExecutor} that calls the underlying method via reflection.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionCommandExecutor implements CommandExecutor {
    private final Object instance;
    private final Method method;

    public ReflectionCommandExecutor(Object instance, Method method) {
        this.instance = Objects.requireNonNull(instance);
        this.method = Objects.requireNonNull(method);
    }

    @Override
    public void execute(CommandArgs args, OutputPrinter outputPrinter) throws Exception {
        // Fetch all params.
        final List<Object> reflectionArgs = ((PrivilegedCommandArgs) args).getArgValues();

        // Add the outputPrinter as the first arg.
        reflectionArgs.add(0, outputPrinter);

        // Invoke method.
        try {
            method.invoke(instance, reflectionArgs.toArray());
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }
}

/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.reflection.method;

import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.command.CommandExecutor;
import com.github.ykrasik.jaci.command.CommandOutputPromise;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A {@link CommandExecutor} that calls the underlying {@link Method} via reflection.
 * Keeps an instance of a  {@link CommandOutputPromise}, which the method may call in it's implementation.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionCommandExecutor implements CommandExecutor {
    private final CommandOutputPromise outputPromise;
    private final Object instance;
    private final Method method;

    public ReflectionCommandExecutor(CommandOutputPromise outputPromise, Object instance, Method method) {
        this.outputPromise = Objects.requireNonNull(outputPromise, "outputPromise");
        this.instance = Objects.requireNonNull(instance, "instance");
        this.method = Objects.requireNonNull(method, "method");
    }

    @Override
    public void execute(CommandOutput output, CommandArgs args) throws Exception {
        // The underlying method may access a CommandOutput field which is expected to be injected.
        // This is implemented by injecting a CommandOutputPromise into the field, and setting it's value
        // before calling the method.
        outputPromise.setOutput(output);

        // Invoke
        method.invoke(instance, args.getArgs().toArray());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReflectionCommandExecutor{");
        sb.append("outputPromise=").append(outputPromise);
        sb.append(", instance=").append(instance);
        sb.append(", method=").append(method);
        sb.append('}');
        return sb.toString();
    }
}

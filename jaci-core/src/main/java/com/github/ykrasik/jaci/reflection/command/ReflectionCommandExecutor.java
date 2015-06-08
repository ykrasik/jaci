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

package com.github.ykrasik.jaci.reflection.command;

import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.command.CommandExecutor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A {@link CommandExecutor} that calls the underlying {@link Method} via reflection.
 *
 * @author Yevgeny Krasik
 */
@ToString
@RequiredArgsConstructor
public class ReflectionCommandExecutor implements CommandExecutor {
    @NonNull private final Object instance;
    @NonNull private final Method method;

    @Override
    public void execute(CommandOutput output, CommandArgs args) throws Exception {
        // Add output as first arg.
        final List<Object> reflectionArgs = args.prependArg(output);

        // Invoke method.
        method.invoke(instance, reflectionArgs.toArray());
    }
}

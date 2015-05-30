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

package com.github.ykrasik.jemi.core.annotation.command;

import com.github.ykrasik.jemi.api.CommandOutput;
import com.github.ykrasik.jemi.core.command.CommandArgs;
import com.github.ykrasik.jemi.core.command.CommandExecutor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link CommandExecutor} that calls the underlying method via reflection.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor
public class ReflectionCommandExecutor implements CommandExecutor {
    @NonNull private final Object instance;
    @NonNull private final Method method;

    @Override
    public void execute(CommandOutput output, CommandArgs args) throws Exception {
        final List<Object> reflectionArgs = new ArrayList<>(args.getArgs());

        // Add output as first arg.
        reflectionArgs.add(0, output);

        // Invoke method.
        method.invoke(instance, reflectionArgs.toArray());
    }
}

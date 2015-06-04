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

package com.github.ykrasik.jemi.core.reflection;

import com.github.ykrasik.jemi.api.CommandPath;
import com.github.ykrasik.jemi.core.reflection.command.ReflectionMethodProcessor;
import com.github.ykrasik.jemi.core.command.CommandDef;
import com.github.ykrasik.jemi.core.path.ParsedPath;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes a class and creates {@link CommandDef}s from qualifying methods.<br>
 *
 * @author Yevgeny Krasik
 */
public class ReflectionClassProcessor {
    private final ReflectionMethodProcessor methodProcessor;

    public ReflectionClassProcessor() {
        this(new ReflectionMethodProcessor());
    }

    /**
     * Package-visible for testing.
     */
    ReflectionClassProcessor(@NonNull ReflectionMethodProcessor methodProcessor) {
        this.methodProcessor = methodProcessor;
    }

    /**
     * Process the object and return a {@link Map} from a {@code path} to a {@link List} of {@link CommandDef}s
     * that were defined for that path.
     *
     * @param instance Object to process.
     * @return The {@link CommandDef}s that were extracted out of the object.
     */
    public Map<String, List<CommandDef>> processObject(@NonNull Object instance) {
        final Class<?> clazz = instance.getClass();

        // All method paths will be appended to the class's top level path.
        final ParsedPath topLevelPath = getTopLevelPath(clazz);

        final ClassContext context = new ClassContext(topLevelPath);

        // Create commands from all qualifying methods..
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            processMethod(context, instance, method);
        }

        return context.commandPaths;
    }

    private void processMethod(ClassContext context, Object instance, Method method) {
        // Commands can only be created from qualifying methods.
        final Opt<CommandDef> commandDef = methodProcessor.process(instance, method);
        if (!commandDef.isPresent()) {
            return;
        }

        final ParsedPath commandPath = getCommandPath(method);
        context.addCommandDef(commandPath, commandDef.get());
    }

    private ParsedPath getTopLevelPath(Class<?> clazz) {
        final CommandPath annotation = clazz.getAnnotation(CommandPath.class);
        if (annotation != null) {
            if (annotation.override()) {
                throw new IllegalArgumentException("Top level paths cannot have override=true! class=" + clazz);
            }
            return fromAnnotation(annotation);
        } else {
            // Class does not declare a top level path, set it to root.
            return ParsedPath.root();
        }
    }

    private ParsedPath getCommandPath(Method method) {
        final CommandPath annotation = method.getAnnotation(CommandPath.class);
        if (annotation != null) {
            return fromAnnotation(annotation);
        } else {
            // Method does not declare a path, set it to an empty path.
            return ParsedPath.empty();
        }
    }

    private ParsedPath fromAnnotation(CommandPath annotation) {
        return new ParsedPath(annotation.value(), annotation.override());
    }

    @RequiredArgsConstructor
    private static class ClassContext {
        private final ParsedPath topLevelPath;

        private final Map<String, List<CommandDef>> commandPaths = new HashMap<>();

        public void addCommandDef(ParsedPath commandPath, CommandDef commandDef) {
            // Compose the top level path of the declaring class with the command path.
            final ParsedPath composedPath = topLevelPath.compose(commandPath);

            final String path = composedPath.getPath();
            List<CommandDef> commands = commandPaths.get(path);
            if (commands == null) {
                commands = new ArrayList<>();
                commandPaths.put(path, commands);
            }
            commands.add(commandDef);
        }
    }
}

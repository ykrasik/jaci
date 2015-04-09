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

package com.github.ykrasik.jerminal.core.annotation;

import com.github.ykrasik.jerminal.api.CommandPath;
import com.github.ykrasik.jerminal.core.annotation.command.AnnotationCommandFactory;
import com.github.ykrasik.jerminal.core.command.CommandDef;
import com.github.ykrasik.jerminal.util.opt.Opt;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes a class and creates {@link CommandDef commandDef}s from methods annotated with qualifying annotations.<br>
 *
 * @author Yevgeny Krasik
 */
public class AnnotationProcessor {
    private final AnnotationCommandFactory commandFactory;

    public AnnotationProcessor() {
        this(new AnnotationCommandFactory());
    }

    /**
     * For testing.
     */
    AnnotationProcessor(@NonNull AnnotationCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Process the object and return the commands that were defined in the object's class with annotations.<br>
     * Never returns null.
     *
     * @param instance Object to process.
     * @return The commands and global commands that were defined in the object's class through annotations.
     */
    public Map<String, List<CommandDef>> processObject(@NonNull Object instance) {
        final Class<?> clazz = instance.getClass();

        // All method paths will be appended to the class's top level path.
        final AnnotatedCommandPath topLevelPath = getTopLevelPath(clazz);

        final Map<String, List<CommandDef>> commandPaths = new HashMap<>();

        // Create commands from all methods that are annotated with qualifying annotations.
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // If the method wasn't annotated, a command won't be created.
            final Opt<CommandDef> commandDef = commandFactory.createCommand(instance, method);
            if (!commandDef.isPresent()) {
                continue;
            }

            // Compose the top level path of the declaring class with the method's path.
            final AnnotatedCommandPath localPath = getLocalPath(method);
            final AnnotatedCommandPath composedPath = topLevelPath.compose(localPath);

            final String path = composedPath.path();
            List<CommandDef> commands = commandPaths.get(path);
            if (commands == null) {
                commands = new ArrayList<>();
                commandPaths.put(path, commands);
            }
            commands.add(commandDef.get());
        }

        return commandPaths;
    }

    private AnnotatedCommandPath getTopLevelPath(Class<?> clazz) {
        final CommandPath annotation = clazz.getAnnotation(CommandPath.class);
        if (annotation != null) {
            if (annotation.override()) {
                throw new IllegalArgumentException("Top level paths cannot be override=true! class=" + clazz);
            }
            return fromAnnotation(annotation);
        } else {
            // Class does not declare a top level path, set it to root.
            return AnnotatedCommandPath.root();
        }
    }

    private AnnotatedCommandPath getLocalPath(Method method) {
        final CommandPath annotation = method.getAnnotation(CommandPath.class);
        if (annotation != null) {
            return fromAnnotation(annotation);
        } else {
            // Method does not declare a path, set it to an empty path.
            return AnnotatedCommandPath.empty();
        }
    }

    private AnnotatedCommandPath fromAnnotation(CommandPath annotation) {
        return AnnotatedCommandPath.from(annotation.value(), annotation.override());
    }
}

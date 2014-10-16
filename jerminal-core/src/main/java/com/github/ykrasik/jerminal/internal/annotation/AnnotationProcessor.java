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

import com.github.ykrasik.jerminal.api.annotation.ShellPath;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.google.common.base.Optional;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Processes a class and creates {@link com.github.ykrasik.jerminal.api.filesystem.command.Command}s based on annotations.<br>
 * In order to be eligible for annotation processing, the class being processed <b>must</b> provide a no-args constructor.<br>
 *
 * @author Yevgeny Krasik
 */
public class AnnotationProcessor {
    private static final Class<?>[] NO_ARGS_TYPE = {};
    private static final Object[] NO_ARGS = {};

    private final AnnotationCommandFactory commandFactory;

    public AnnotationProcessor(AnnotationCommandFactory commandFactory) {
        this.commandFactory = Objects.requireNonNull(commandFactory);
    }

    /**
     * Process a class and return the commands and global commands that were defined in this class
     * with annotations.<br>
     * Never returns null.
     *
     * @param clazz Class to process.
     * @return A {@link AnnotationProcessorReturnValue} with the commands and global commands that were
     *         defined in this class through annotations.
     * @throws IllegalArgumentException If the class doesn't have a no-args constructor.
     * @throws ShellException If an error occurs while instantiating the class.
     */
    public AnnotationProcessorReturnValue processClass(Class<?> clazz) {
        final Object instance = createInstance(clazz);
        return processObject(instance);
    }

    /**
     * Process the object and return the commands and global commands that were defined in the object's class
     * with annotations.<br>
     * Intended for use when the commands need to operate on a specific object, due to internal state.
     * The caller should instantiate the object and pass it to this.
     * Never returns null.
     *
     * @param instance Object to process.
     * @return A {@link AnnotationProcessorReturnValue} with the commands and global commands that were
     *         defined in the object's class through annotations.
     */
    public AnnotationProcessorReturnValue processObject(Object instance) {
        final Class<?> clazz = instance.getClass();

        // All method paths will be appended to the class's top level path.
        final AnnotatedPath topLevelPath = getTopLevelPath(clazz);

        final List<Command> globalCommands = new ArrayList<>();
        final Map<String, List<Command>> commandPaths = new HashMap<>();

        // Find all methods annotated with @Command or @ToggleCommand and instantiate the commands.
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // If the method wasn't annotated, a command won't be created.
            final Optional<Command> commandOptional = commandFactory.createCommand(instance, method);
            if (!commandOptional.isPresent()) {
                continue;
            }

            // Compose the top level path of the declaring class with the method's path.
            final Command command = commandOptional.get();
            final AnnotatedPath localPath = getLocalPath(method);
            final AnnotatedPath composedPath = topLevelPath.compose(localPath);

            // The command will either be a global or local command, depending on the annotations.
            if (composedPath.isGlobal()) {
                globalCommands.add(command);
            } else {
                final String path = composedPath.getPath();
                List<Command> commands = commandPaths.get(path);
                if (commands == null) {
                    commands = new ArrayList<>();
                    commandPaths.put(path, commands);
                }
                commands.add(command);
            }
        }

        return new AnnotationProcessorReturnValue(globalCommands, commandPaths);
    }

    private Object createInstance(Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor(NO_ARGS_TYPE);
            return constructor.newInstance(NO_ARGS);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class doesn't have a no-args constructor: " + clazz, e);
        } catch (Exception e) {
            throw new ShellException("Error instantiating new instance of type: " + clazz, e);
        }
    }

    private AnnotatedPath getTopLevelPath(Class<?> clazz) {
        final ShellPath annotation = clazz.getAnnotation(ShellPath.class);
        if (annotation != null) {
            return AnnotatedPath.fromAnnotation(annotation);
        } else {
            // Class does not declare a top level path, set it to root.
            return AnnotatedPath.root();
        }
    }

    private AnnotatedPath getLocalPath(Method method) {
        final ShellPath annotation = method.getAnnotation(ShellPath.class);
        if (annotation != null) {
            return AnnotatedPath.fromAnnotation(annotation);
        } else {
            // Method does not declare a top level path, set it to an empty path.
            return AnnotatedPath.empty();
        }
    }
}

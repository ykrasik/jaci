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

import com.github.ykrasik.jerminal.api.annotation.*;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.command.toggle.StateAccessor;
import com.github.ykrasik.jerminal.api.command.toggle.ToggleCommandBuilder;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.internal.command.CommandImpl;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
// FIXME: Needs some refactoring
public class AnnotationProcessor {
    private static final Class<?>[] NO_ARGS = {};

    // FIXME: JavaDoc
    public <T> void process(ShellFileSystem fileSystem, Class<T> clazz) {
        final Object instance = createInstance(clazz);

        // All method paths will be appended to the class's top level path.
        final AnnotatedPath topLevelPath = getTopLevelPath(clazz);

        final List<Command> globalCommands = new ArrayList<>();
        final Map<String, List<Command>> commandPaths = new HashMap<>();

        // Find all methods annotated with @Command or @ToggleCommand and instantiate the commands.
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // If the method wasn't annotated, a command won't be created.
            final Command command = createCommand(instance, method);
            if (command != null) {
                // Compose the top level path of the declaring class with the method's path.
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
        }

        // Add all collected global and local commands to the file system.
        fileSystem.addGlobalCommands(globalCommands);
        for (Entry<String, List<Command>> entry : commandPaths.entrySet()) {
            fileSystem.addCommands(entry.getKey(), entry.getValue());
        }
    }

    private Object createInstance(Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor(NO_ARGS);
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

    private Command createCommand(Object instance, Method method) {
        // Check if method has the @Command annotation.
        final com.github.ykrasik.jerminal.api.annotation.Command commandAnnotation = method.getAnnotation(com.github.ykrasik.jerminal.api.annotation.Command.class);
        if (commandAnnotation != null) {
            return doCreateCommand(instance, method, commandAnnotation);
        }

        // Check if method has the @ToggleCommand annotation.
        final ToggleCommand toggleCommandAnnotation = method.getAnnotation(ToggleCommand.class);
        if (toggleCommandAnnotation != null) {
            return createToggleCommand(instance, method, toggleCommandAnnotation);
        }

        // This method is not annotated with any command annotation.
        return null;
    }

    private Command doCreateCommand(Object instance, Method method, com.github.ykrasik.jerminal.api.annotation.Command annotation) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        // First parameter must be the outputPrinter.
        // FIXME: Make outputPrinter optional.
        if (parameterTypes.length == 0 || parameterTypes[0] != OutputPrinter.class) {
            final String message = String.format("Commands must receive an %s as their first parameter: class=%s, method=%s", OutputPrinter.class, method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message);
        }

        // Create command parameters from method parameters.
        final List<CommandParam> params = new ArrayList<>(parameterTypes.length);
        for (int i = 1; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            final Annotation[] annotations = parameterAnnotations[i];
            final CommandParam param = createCommandParam(parameterType, annotations, i);
            params.add(param);
        }

        final String commandName = method.getName();
        final String description = annotation.value();
        final ReflectionCommandExecutor executor = new ReflectionCommandExecutor(instance, method);
        return new CommandImpl(commandName, description, params, executor);
    }

    private Command createToggleCommand(Object instance, Method method, ToggleCommand annotation) {
        final Class<?> returnType = method.getReturnType();
        if (returnType != StateAccessor.class) {
            final String message = String.format("Methods annotated with @ToggleCommand must return a %s: class=%s, method=%s", method.getDeclaringClass(), method.getName(), StateAccessor.class);
            throw new IllegalArgumentException(message);
        }

        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            final String message = String.format("Methods annotated with @ToggleCommand must not take any parameters: class=%s, method=%s", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message);
        }

        try {
            final StateAccessor accessor = (StateAccessor) method.invoke(instance, NO_ARGS);
            final ToggleCommandBuilder builder = new ToggleCommandBuilder(method.getName(), accessor);

            final String description = annotation.value();
            if (!description.trim().isEmpty()) {
                builder.setCommandDescription(description);
            }

            return builder.build();
        } catch (Exception e) {
            final String message = String.format("Error creating ToggleCommand: class=%s, method=%s", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message, e);
        }
    }

    private CommandParam createCommandParam(Class<?> parameterType, Annotation[] annotations, int index) {
        // Translate the method parameters into CommandParams.
        if (parameterType == String.class) {
            final StringParam annotation = findAnnotation(annotations, StringParam.class);
            return AnnotationCommandParamFactory.createStringParam(annotation, index);
        }

        if (parameterType == Boolean.class || parameterType == Boolean.TYPE) {
            final FlagParam flagAnnotation = findAnnotation(annotations, FlagParam.class);
            if (flagAnnotation != null) {
                return AnnotationCommandParamFactory.createFlagParam(flagAnnotation, index);
            } else {
                final BoolParam annotation = findAnnotation(annotations, BoolParam.class);
                return AnnotationCommandParamFactory.createBooleanParam(annotation, index);
            }
        }

        if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            final IntParam annotation = findAnnotation(annotations, IntParam.class);
            return AnnotationCommandParamFactory.createIntParam(annotation, index);
        }

        if (parameterType == Double.class || parameterType == Double.TYPE) {
            final DoubleParam annotation = findAnnotation(annotations, DoubleParam.class);
            return AnnotationCommandParamFactory.createDoubleParam(annotation, index);
        }

        throw new IllegalArgumentException("Invalid parameterType: " + parameterType);
    }

    @SuppressWarnings("unchecked")
    private <T> T findAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == clazz) {
                return (T) annotation;
            }
        }
        return null;
    }
}

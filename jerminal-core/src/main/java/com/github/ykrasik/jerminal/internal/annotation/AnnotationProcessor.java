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

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.annotation.*;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.flag.FlagParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.DoubleParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.api.command.toggle.StateAccessor;
import com.github.ykrasik.jerminal.api.command.toggle.ToggleCommandBuilder;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.internal.command.CommandImpl;
import com.github.ykrasik.jerminal.internal.command.PrivilegedCommandArgs;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
// FIXME: Needs some refactoring
public class AnnotationProcessor {
    private static final Class<?>[] NO_ARGS = {};

    // FIXME: JavaDoc
    public <T> void process(ShellFileSystem fileSystem, Class<T> clazz) {
        final Object reference = createReference(clazz);
        // All method paths will be appended to the class's top level path.
        final String topLevelPath = getTopLevelPath(clazz);

        final Method[] methods = clazz.getMethods();
        final List<com.github.ykrasik.jerminal.api.filesystem.command.Command> globalCommands = new ArrayList<>();
        for (Method method : methods) {
            final com.github.ykrasik.jerminal.api.filesystem.command.Command command = createCommand(reference, method);
            if (command != null) {
                // If the method wasn't annotated, a command won't be created.
                final boolean globalCommand = isGlobalCommand(method);
                if (globalCommand) {
                    globalCommands.add(command);
                } else {
                    // FIXME: Allow path composition.
                    fileSystem.addCommands(topLevelPath, command);
                }
            }
        }

        fileSystem.addGlobalCommands(globalCommands);
    }

    private Object createReference(Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor(NO_ARGS);
            return constructor.newInstance(NO_ARGS);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class doesn't have a no-args contrstructor: " + clazz, e);
        } catch (Exception e) {
            throw new ShellException("Error creating new instance of type: " + clazz, e);
        }
    }

    private String getTopLevelPath(Class<?> clazz) {
        final ShellPath topLevelPathAnnotation = clazz.getAnnotation(ShellPath.class);
        if (topLevelPathAnnotation != null) {
            return topLevelPathAnnotation.value();
        } else {
            // Class does not declare a top level path, set it to root.
            return ShellConstants.FILE_SYSTEM_DELIMITER;
        }
    }

    private boolean isGlobalCommand(Method method) {
        // FIXME: A command should be global if the topLevelPath is global and it doesn't define it's own,
        // FIXME: or if the topLevelPath isn't global, but the command overrides it.
        final ShellPath shellPathAnnotation = method.getAnnotation(ShellPath.class);
        return shellPathAnnotation != null && shellPathAnnotation.global();
    }

    private com.github.ykrasik.jerminal.api.filesystem.command.Command createCommand(Object reference, Method method) {
        final Command commandAnnotation = method.getAnnotation(Command.class);
        if (commandAnnotation != null) {
            return doCreateCommand(reference, method, commandAnnotation);
        } else {
            // Check if this is a toggle command.
            final ToggleCommand toggleCommandAnnotation = method.getAnnotation(ToggleCommand.class);
            if (toggleCommandAnnotation != null) {
                return createToggleCommand(reference, method, toggleCommandAnnotation);
            } else {
                // This method is not annotated with any command annotation.
                return null;
            }
        }
    }

    private com.github.ykrasik.jerminal.api.filesystem.command.Command doCreateCommand(Object reference, Method method, Command annotation) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final List<CommandParam> params = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            final Annotation[] annotations = parameterAnnotations[i];
            final CommandParam param = createCommandParam(parameterType, annotations, i);
            params.add(param);
        }

        final String commandName = method.getName();
        final String description = annotation.value();
        final ReflectionCommandExecutor executor = new ReflectionCommandExecutor(reference, method);
        return new CommandImpl(commandName, description, params, executor);
    }

    private com.github.ykrasik.jerminal.api.filesystem.command.Command createToggleCommand(Object reference, Method method, ToggleCommand annotation) {
        final Class<?> returnType = method.getReturnType();
        if (returnType != StateAccessor.class) {
            final String message = String.format("Methods annotated with @ToggleCommand must return a StateAccessor: class=%s, method=%s", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message);
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            final String message = String.format("Methods annotated with @ToggleCommand must not take any parameters: class=%s, method=%s", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message);
        }

        try {
            final StateAccessor accessor = (StateAccessor) method.invoke(reference, NO_ARGS);
            final ToggleCommandBuilder builder = new ToggleCommandBuilder(method.getName(), accessor);

            final String description = annotation != null ? annotation.value() : "";
            if (!description.trim().isEmpty()) {
                builder.setCommandDescription(description);
            }

            return builder.build();

        } catch (Exception e) {
            final String message = String.format("Error creating StateAccessor: class=%s, method=%s", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message);
        }
    }

    private CommandParam createCommandParam(Class<?> parameterType, Annotation[] annotations, int index) {
        if (parameterType == String.class) {
            final StringParam annotation = findAnnotation(annotations, StringParam.class);
            return createStringParam(annotation, index);
        }

        if (parameterType == Boolean.class || parameterType == Boolean.TYPE) {
            final FlagParam flagAnnotation = findAnnotation(annotations, FlagParam.class);
            if (flagAnnotation != null) {
                return createFlagParam(flagAnnotation, index);
            } else {
                final BoolParam annotation = findAnnotation(annotations, BoolParam.class);
                return createBooleanParam(annotation, index);
            }
        }

        if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            final IntParam annotation = findAnnotation(annotations, IntParam.class);
            return createIntParam(annotation, index);
        }

        if (parameterType == Double.class || parameterType == Double.TYPE) {
            final DoubleParam annotation = findAnnotation(annotations, DoubleParam.class);
            return createDoubleParam(annotation, index);
        }

        throw new IllegalArgumentException("Invalid parameterType: " + parameterType);
    }

    private CommandParam createStringParam(StringParam annotation, int index) {
        final String name = getOrGenerateName(annotation != null ? annotation.value() : "", "str", index);
        final StringParamBuilder builder = new StringParamBuilder(name);

        final String description = annotation != null ? annotation.description() : "";
        if (!description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        final boolean optional = annotation != null && annotation.optional();
        if (optional) {
            final String defaultValue = annotation.defaultValue();
            builder.setOptional(defaultValue);
        }

        final String[] accepts = annotation != null ? annotation.acceptsValues() : null;
        if (accepts != null) {
            builder.setConstantAcceptableValues(accepts);
        }

        return builder.build();
    }

    private CommandParam createFlagParam(FlagParam annotation, int index) {
        final String name = getOrGenerateName(annotation != null ? annotation.value() : "", "flag", index);
        final FlagParamBuilder builder = new FlagParamBuilder(name);

        final String description = annotation != null ? annotation.description() : "";
        if (!description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        return builder.build();
    }

    private CommandParam createBooleanParam(BoolParam annotation, int index) {
        final String name = getOrGenerateName(annotation != null ? annotation.value() : "", "bool", index);
        final BooleanParamBuilder builder = new BooleanParamBuilder(name);

        final String description = annotation != null ? annotation.description() : "";
        if (!description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        final boolean optional = annotation != null && annotation.optional();
        if (optional) {
            final boolean defaultValue = annotation.defaultValue();
            builder.setOptional(defaultValue);
        }

        return builder.build();
    }

    private CommandParam createIntParam(IntParam annotation, int index) {
        final String name = getOrGenerateName(annotation != null ? annotation.value() : "", "int", index);
        final IntegerParamBuilder builder = new IntegerParamBuilder(name);

        final String description = annotation != null ? annotation.description() : "";
        if (!description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        final boolean optional = annotation != null && annotation.optional();
        if (optional) {
            final int defaultValue = annotation.defaultValue();
            builder.setOptional(defaultValue);
        }

        return builder.build();
    }

    private CommandParam createDoubleParam(DoubleParam annotation, int index) {
        final String name = getOrGenerateName(annotation != null ? annotation.value() : "", "double", index);
        final DoubleParamBuilder builder = new DoubleParamBuilder(name);

        final String description = annotation != null ? annotation.description() : "";
        if (!description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        final boolean optional = annotation != null && annotation.optional();
        if (optional) {
            final double defaultValue = annotation.defaultValue();
            builder.setOptional(defaultValue);
        }

        return builder.build();
    }

    private String getOrGenerateName(String name, String prefix, int index) {
        final String trimmedName = name.trim();
        if (!trimmedName.isEmpty()) {
            return trimmedName;
        } else {
            return prefix + "Param" + (index + 1);
        }
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

    private static class ReflectionCommandExecutor implements CommandExecutor {
        private final Object reference;
        private final Method method;

        private ReflectionCommandExecutor(Object reference, Method method) {
            this.reference = Objects.requireNonNull(reference);
            this.method = Objects.requireNonNull(method);
        }

        @Override
        public void execute(CommandArgs args, OutputPrinter outputPrinter) throws Exception {
            // Fetch all params and call method via reflection.
            final Object[] reflectionArgs = ((PrivilegedCommandArgs) args).toObjectArray();
            method.invoke(reference, reflectionArgs);
        }
    }
}

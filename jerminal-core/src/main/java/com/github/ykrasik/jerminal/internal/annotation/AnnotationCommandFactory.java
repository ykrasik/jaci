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

package com.github.ykrasik.jerminal.internal.annotation;

import com.github.ykrasik.jerminal.api.annotation.ToggleCommand;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.command.toggle.StateAccessor;
import com.github.ykrasik.jerminal.api.command.toggle.ToggleCommandBuilder;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.internal.annotation.param.AnnotationParamFactory;
import com.github.ykrasik.jerminal.internal.util.ReflectionParameter;
import com.github.ykrasik.jerminal.internal.util.ReflectionUtils;
import com.google.common.base.Optional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.github.ykrasik.jerminal.internal.util.StringUtils.getOptionalString;

/**
 * Creates {@link Command}s out of {@link Method}s if they are annotated.<br>
 * Empty names will be replaced with a generated name, and empty descriptions will use default values.
 * @see com.github.ykrasik.jerminal.api.annotation.Command
 * @see ToggleCommand
 *
 * @author Yevgeny Krasik
 */
public class AnnotationCommandFactory {
    private final AnnotationParamFactory paramFactory;

    public AnnotationCommandFactory() {
        this(new AnnotationParamFactory());
    }

    /**
     * For testing.
     */
    AnnotationCommandFactory(AnnotationParamFactory paramFactory) {
        this.paramFactory = Objects.requireNonNull(paramFactory);
    }

    /**
     * @param instance Instance that will act as 'this' when invoking the method through reflection.
     * @param method Method to be processed.
     * @return A {@link Command} if the method is annotated with any supported annotation.
     */
    public Optional<Command> createCommand(Object instance, Method method) {
        // Check if method has the @Command annotation.
        final com.github.ykrasik.jerminal.api.annotation.Command commandAnnotation = method.getAnnotation(com.github.ykrasik.jerminal.api.annotation.Command.class);
        if (commandAnnotation != null) {
            try {
                return Optional.of(createCommand(instance, method, commandAnnotation));
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Error creating command: class=%s, method=%s", method.getDeclaringClass(), method.getName()), e);
            }
        }

        // Check if method has the @ToggleCommand annotation.
        final ToggleCommand toggleCommandAnnotation = method.getAnnotation(ToggleCommand.class);
        if (toggleCommandAnnotation != null) {
            try {
                return Optional.of(createToggleCommand(instance, method, toggleCommandAnnotation));
            } catch (Exception e){
                final String message = String.format("Error creating toggle command: class=%s, method=%s", method.getDeclaringClass(), method.getName());
                throw new IllegalArgumentException(message, e);
            }
        }

        // This method is not annotated with any command annotation.
        return Optional.absent();
    }

    private Command createCommand(Object instance, Method method, com.github.ykrasik.jerminal.api.annotation.Command annotation) {
        final String name = getOrGenerateCommandName(annotation.value(), method);
        final CommandBuilder builder = new CommandBuilder(name);

        final Optional<String> description = getOptionalString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        // Reflect method parameters.
        final List<ReflectionParameter> parameters = ReflectionUtils.reflectMethodParameters(method);

        // If present, the outputPrinter must be the first arg.
        final boolean hasOutputPrinter = !parameters.isEmpty() && isOutputPrinter(parameters.get(0).getParameterType());
        builder.setExecutor(new ReflectionCommandExecutor(instance, method, hasOutputPrinter));

        for (int i = hasOutputPrinter ? 1 : 0; i < parameters.size(); i++) {
            final ReflectionParameter param = parameters.get(i);
            final Class<?> parameterType = param.getParameterType();
            if (isOutputPrinter(parameterType)) {
                final String message = String.format(
                    "OutputPrinters must either be the first argument of a method or not be present at all: class=%s, method=%s",
                    method.getDeclaringClass(), method.getName()
                );
                throw new IllegalArgumentException(message);
            }
            final CommandParam commandParam = paramFactory.createParam(instance, param);
            builder.addParam(commandParam);
        }

        return builder.build();
    }

    private boolean isOutputPrinter(Class<?> clazz) {
        return OutputPrinter.class.isAssignableFrom(clazz);
    }

    private Command createToggleCommand(Object instance, Method method, ToggleCommand annotation) {
        assertReturnValue(method, StateAccessor.class, ToggleCommand.class);
        assertNoParameters(method, ToggleCommand.class);

        final String name = getOrGenerateCommandName(annotation.value(), method);
        final ToggleCommandBuilder builder = new ToggleCommandBuilder(name);

        final Optional<String> commandDescription = getOptionalString(annotation.description());
        if (commandDescription.isPresent()) {
            builder.setCommandDescription(commandDescription.get());
        }

        final Optional<String> paramName = getOptionalString(annotation.paraName());
        if (paramName.isPresent()) {
            builder.setParamName(paramName.get());
        }

        final Optional<String> paramDescription = getOptionalString(annotation.paramDescription());
        if (paramDescription.isPresent()) {
            builder.setParamDescription(paramDescription.get());
        }

        final StateAccessor accessor = ReflectionUtils.invokeNoArgs(instance, method, StateAccessor.class);
        builder.setAccessor(accessor);

        return builder.build();
    }

    private String getOrGenerateCommandName(String name, Method method) {
        final String trimmedName = name.trim();
        if (!trimmedName.isEmpty()) {
            return trimmedName;
        } else {
            return method.getName();
        }
    }

    private void assertReturnValue(Method method, Class<?> expectedReturnValue, Class<?> annotation) {
        final Class<?> returnType = method.getReturnType();
        if (returnType != expectedReturnValue) {
            final String message = String.format(
                "Methods annotated with %s must return a %s: class=%s, method=%s",
                annotation, expectedReturnValue, method.getDeclaringClass(), method.getName()
            );
            throw new IllegalArgumentException(message);
        }
    }

    private void assertNoParameters(Method method, Class<?> annotation) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            final String message = String.format(
                "Methods annotated with %s must not take any parameters: class=%s, method=%s",
                annotation, method.getDeclaringClass(), method.getName()
            );
            throw new IllegalArgumentException(message);
        }
    }
}

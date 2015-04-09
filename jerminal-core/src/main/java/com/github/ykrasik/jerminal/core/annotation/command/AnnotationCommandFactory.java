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

package com.github.ykrasik.jerminal.core.annotation.command;

import com.github.ykrasik.jerminal.api.Command;
import com.github.ykrasik.jerminal.api.CommandOutput;
import com.github.ykrasik.jerminal.api.ToggleCommand;
import com.github.ykrasik.jerminal.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jerminal.core.annotation.param.AnnotationParamFactory;
import com.github.ykrasik.jerminal.core.command.CommandDef;
import com.github.ykrasik.jerminal.core.param.ParamDef;
import com.github.ykrasik.jerminal.util.opt.Opt;
import com.github.ykrasik.jerminal.util.reflection.ReflectionParameter;
import com.github.ykrasik.jerminal.util.reflection.ReflectionUtils;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;

import static com.github.ykrasik.jerminal.util.string.StringUtils.getNonEmptyString;
import static com.github.ykrasik.jerminal.util.string.StringUtils.getNonEmptyStringOrDefault;

/**
 * Creates {@link CommandDef}s out of {@link Method}s if they are annotated.<br>
 * Empty names will be replaced with a the method's name, and empty descriptions will use default values.<br>
 * Supports {@link Command} and {@link ToggleCommand}.
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
    AnnotationCommandFactory(@NonNull AnnotationParamFactory paramFactory) {
        this.paramFactory = paramFactory;
    }

    /**
     * Process the method and create a {@link CommandDef} out of it, if it is annotated with a qualifying annotation.
     *
     * @param instance Instance that will act as 'this' when invoking the method through reflection.
     * @param method Method to be processed.
     * @return A {@link CommandDef} if the method is annotated with any supported annotation.
     */
    public Opt<CommandDef> createCommand(@NonNull Object instance, @NonNull Method method) {
        try {
            return doCreateCommand(instance, method);
        } catch (Exception e) {
            final String message = String.format("Error creating command: class=%s, method=%s", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message, e);
        }
    }

    private Opt<CommandDef> doCreateCommand(Object instance, Method method) {
        // Check if method has the @Command annotation.
        final Command commandAnnotation = method.getAnnotation(Command.class);
        if (commandAnnotation != null) {
            return Opt.of(createCommand(instance, method, commandAnnotation));
        }

        // Check if method has the @ToggleCommand annotation.
        final ToggleCommand toggleCommandAnnotation = method.getAnnotation(ToggleCommand.class);
        if (toggleCommandAnnotation != null) {
            return Opt.of(createToggleCommand(instance, method, toggleCommandAnnotation));
        }

        // This method is not annotated with any compatible command annotation.
        return Opt.absent();
    }

    private CommandDef createCommand(Object instance, Method method, Command annotation) {
        // Reflect method params.
        final List<ReflectionParameter> params = ReflectionUtils.reflectMethodParameters(method);

        // First parameter must be CommandOutput.
        if (params.isEmpty() || !isCommandOutput(params.get(0).getParameterType())) {
            throw new IllegalArgumentException("First parameter of a command must be of type " + CommandOutput.class);
        }

        final CommandDef.Builder builder = new CommandDef.Builder(getNonEmptyStringOrDefault(annotation.value(), method.getName()));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        for (int i = 1; i < params.size(); i++) {
            final ReflectionParameter param = params.get(i);
            final ParamDef<?> paramDef = paramFactory.createParam(instance, param);
            builder.addParam(paramDef);
        }

        builder.setExecutor(new ReflectionCommandExecutor(instance, method));

        return builder.build();
    }

    private boolean isCommandOutput(Class<?> clazz) {
        return CommandOutput.class.isAssignableFrom(clazz);
    }

    private CommandDef createToggleCommand(Object instance, Method method, ToggleCommand annotation) {
        assertReturnValue(method, ToggleCommandStateAccessor.class, ToggleCommand.class);
        assertNoParameters(method, ToggleCommand.class);
        final ToggleCommandStateAccessor accessor = ReflectionUtils.invokeNoArgs(instance, method);

        final ToggleCommandDefBuilder builder = new ToggleCommandDefBuilder(getNonEmptyStringOrDefault(annotation.value(), method.getName()));
        builder.setAccessor(accessor);

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        final Opt<String> paramName = getNonEmptyString(annotation.paramName());
        if (paramName.isPresent()) {
            builder.setParamName(paramName.get());
        }

        final Opt<String> paramDescription = getNonEmptyString(annotation.paramDescription());
        if (paramDescription.isPresent()) {
            builder.setParamDescription(paramDescription.get());
        }

        return builder.build();
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

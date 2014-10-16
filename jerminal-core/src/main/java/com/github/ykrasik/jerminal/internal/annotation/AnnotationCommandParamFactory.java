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
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.flag.FlagParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.DoubleParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;

import java.lang.annotation.Annotation;

/**
 * Creates {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam}s from param annotations.<br>
 * Param annotations are optional, so this class can also assign sensible defaults when the annotation isn't present.<br>
 * Empty names or descriptions aren't allowed.
 *
 * @author Yevgeny Krasik
 */
public class AnnotationCommandParamFactory {
    /**
     * Create a {@link CommandParam} out of a method parameter with optional annotations, through reflection.
     * Since parameter names aren't always available to be reflected, parameter names can only be set through
     * the annotation. If absent, a name will be generated for the parameter.
     *
     * @param parameterType Type of parameter to create.
     * @param annotations The parameter's annotations. May be empty if parameter has none.
     * @param index The parameter index as a positional parameter. Used to generate a name if required.
     * @return A {@link CommandParam} constructed from the annotation if it was present,
     *         or one with sensible defaults if it wasn't.
     */
    public CommandParam createCommandParam(Class<?> parameterType, Annotation[] annotations, int index) {
        // Translate the method parameters into CommandParams.
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

    @SuppressWarnings("unchecked")
    private <T> T findAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == clazz) {
                return (T) annotation;
            }
        }
        return null;
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

        final String[] accepts = annotation != null ? annotation.accepts() : null;
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
            return prefix + "Param" + index;
        }
    }
}

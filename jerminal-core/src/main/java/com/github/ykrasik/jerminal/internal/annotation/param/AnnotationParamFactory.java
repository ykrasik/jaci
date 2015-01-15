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

package com.github.ykrasik.jerminal.internal.annotation.param;

import com.github.ykrasik.jerminal.api.annotation.*;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.flag.FlagParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.DoubleParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.internal.annotation.ReflectionValuesSupplier;
import com.github.ykrasik.jerminal.internal.util.ReflectionParameter;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import java.util.List;
import java.util.Objects;

import static com.github.ykrasik.jerminal.internal.util.StringUtils.getOptionalString;

/**
 * Creates {@link CommandParam}s from parameter information obtained via reflection.<br>
 * Param annotations are optional and sensible defaults will be assigned when the annotation isn't present.<br>
 * If not provided, parameter names will be generated and descriptions will use a default value.
 *
 * @author Yevgeny Krasik
 */
public class AnnotationParamFactory {
    private final ParamNameGenerator nameGenerator;

    public AnnotationParamFactory() {
        this(new DefaultParamNameGenerator());
    }

    /**
     * For testing
     */
    AnnotationParamFactory(ParamNameGenerator nameGenerator) {
        this.nameGenerator = Objects.requireNonNull(nameGenerator);
    }

    /**
     * Create a {@link CommandParam out of a method parameter with optional annotations, through reflection.
     * Since parameter names aren't always available to be reflected, parameter names can only be set through
     * the annotation. If absent, a name will be generated for the parameter.
     *
     * @param instance The instance that contains the method this parameter is being generated for.
     * @param param Information about the parameter.
     * @return A {@link CommandParam} constructed from the annotation if it was present,
     *         or one with sensible defaults if it wasn't.
     */
    public CommandParam createParam(Object instance, ReflectionParameter param) {
        final Class<?> parameterType = param.getParameterType();

        if (parameterType == String.class) {
            return createStringParam(instance, param);
        }

        if (parameterType == Boolean.class || parameterType == Boolean.TYPE) {
            return createBooleanParam(param);
        }

        if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            final Optional<IntParam> annotation = param.getAnnotation(IntParam.class);
            return createIntParam(param, annotation);
        }

        if (parameterType == Double.class || parameterType == Double.TYPE) {
            final Optional<DoubleParam> annotation = param.getAnnotation(DoubleParam.class);
            return createDoubleParam(param, annotation);
        }

        throw new IllegalArgumentException("Invalid parameterType: " + parameterType);
    }

    private CommandParam createStringParam(Object instance, ReflectionParameter param) {
        final Optional<DynamicStringParam> dynamicStringAnnotation = param.getAnnotation(DynamicStringParam.class);
        if (dynamicStringAnnotation.isPresent()) {
            return createDynamicStringParam(instance, param, dynamicStringAnnotation.get());
        }

        final Optional<StringParam> stringAnnotation = param.getAnnotation(StringParam.class);
        return createStringParam(param, stringAnnotation);
    }

    private CommandParam createDynamicStringParam(Object instance, ReflectionParameter param, DynamicStringParam annotation) {
        final String name = getOrGenerateName(annotation.value(), param);
        final StringParamBuilder builder = new StringParamBuilder(name);

        final Optional<String> description = getOptionalString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        final boolean optional = annotation.optional();
        if (optional) {
            final String defaultValue = annotation.defaultValue();
            builder.setOptional(defaultValue);
        }

        final String supplierName = annotation.supplier();
        final Supplier<List<String>> supplier = new ReflectionValuesSupplier(instance, supplierName);
        builder.setDynamicAcceptableValuesSupplier(supplier);

        return builder.build();
    }

    private CommandParam createStringParam(ReflectionParameter param, Optional<StringParam> annotation) {
        final String name = getOrGenerateName(annotation.isPresent() ? annotation.get().value() : "", param);
        final StringParamBuilder builder = new StringParamBuilder(name);

        final Optional<String> description = annotation.isPresent() ? getOptionalString(annotation.get().description()) : Optional.<String>absent();
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        final boolean optional = annotation.isPresent() && annotation.get().optional();
        if (optional) {
            final String defaultValue = annotation.get().defaultValue();
            builder.setOptional(defaultValue);
        }

        final Optional<String[]> accepts = annotation.isPresent() ? Optional.of(annotation.get().accepts()) : Optional.<String[]>absent();
        if (accepts.isPresent()) {
            builder.setConstantAcceptableValues(accepts.get());
        }

        return builder.build();
    }

    private CommandParam createBooleanParam(ReflectionParameter param) {
        final Optional<FlagParam> flagAnnotation = param.getAnnotation(FlagParam.class);
        if (flagAnnotation.isPresent()) {
            return createFlagParam(param, flagAnnotation.get());
        }

        final Optional<BoolParam> boolAnnotation = param.getAnnotation(BoolParam.class);
        return createBoolParam(param, boolAnnotation);
    }

    private CommandParam createFlagParam(ReflectionParameter param, FlagParam annotation) {
        final String name = getOrGenerateName(annotation.value(), param);
        final FlagParamBuilder builder = new FlagParamBuilder(name);

        final Optional<String> description = getOptionalString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        return builder.build();
    }

    private CommandParam createBoolParam(ReflectionParameter param, Optional<BoolParam> annotation) {
        final String name = getOrGenerateName(annotation.isPresent() ? annotation.get().value() : "", param);
        final BooleanParamBuilder builder = new BooleanParamBuilder(name);

        final Optional<String> description = annotation.isPresent() ? getOptionalString(annotation.get().description()) : Optional.<String>absent();
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        final boolean optional = annotation.isPresent() && annotation.get().optional();
        if (optional) {
            final boolean defaultValue = annotation.get().defaultValue();
            builder.setOptional(defaultValue);
        }

        return builder.build();
    }

    private CommandParam createIntParam(ReflectionParameter param, Optional<IntParam> annotation) {
        final String name = getOrGenerateName(annotation.isPresent() ? annotation.get().value() : "", param);
        final IntegerParamBuilder builder = new IntegerParamBuilder(name);

        final Optional<String> description = annotation.isPresent() ? getOptionalString(annotation.get().description()) : Optional.<String>absent();
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        final boolean optional = annotation.isPresent() && annotation.get().optional();
        if (optional) {
            final int defaultValue = annotation.get().defaultValue();
            builder.setOptional(defaultValue);
        }

        return builder.build();
    }

    private CommandParam createDoubleParam(ReflectionParameter param, Optional<DoubleParam> annotation) {
        final String name = getOrGenerateName(annotation.isPresent() ? annotation.get().value() : "", param);
        final DoubleParamBuilder builder = new DoubleParamBuilder(name);

        final Optional<String> description = annotation.isPresent() ? getOptionalString(annotation.get().description()) : Optional.<String>absent();
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        final boolean optional = annotation.isPresent() && annotation.get().optional();
        if (optional) {
            final double defaultValue = annotation.get().defaultValue();
            builder.setOptional(defaultValue);
        }

        return builder.build();
    }

    private String getOrGenerateName(String name, ReflectionParameter param) {
        final String trimmedName = name.trim();
        if (!trimmedName.isEmpty()) {
            return trimmedName;
        } else {
            return nameGenerator.generateName(param);
        }
    }
}

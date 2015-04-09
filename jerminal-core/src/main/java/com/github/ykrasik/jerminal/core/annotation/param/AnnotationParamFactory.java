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

package com.github.ykrasik.jerminal.core.annotation.param;

import com.github.ykrasik.jerminal.api.BoolParam;
import com.github.ykrasik.jerminal.api.DoubleParam;
import com.github.ykrasik.jerminal.api.IntParam;
import com.github.ykrasik.jerminal.api.StringParam;
import com.github.ykrasik.jerminal.core.param.*;
import com.github.ykrasik.jerminal.util.opt.Opt;
import com.github.ykrasik.jerminal.util.reflection.ReflectionParameter;
import lombok.NonNull;

import static com.github.ykrasik.jerminal.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link ParamDef}s from parameter information obtained via reflection.<br>
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
    AnnotationParamFactory(@NonNull ParamNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    /**
     * Create a {@link ParamDef}s out of a method parameter with optional annotations, through reflection.
     * Since parameter names aren't always available to be reflected, parameter names can only be set through
     * the annotation. If absent, a name will be generated for the parameter.
     *
     * @param instance The instance that contains the method this parameter is being generated for.
     * @param param Information about the parameter.
     * @return A {@link ParamDef} constructed from the annotation if it was present, or one with sensible defaults if it wasn't.
     * @throws IllegalArgumentException If the parameter is of an incompatible type.
     *                                  Only accepts {boolean, int, double} (and their boxed versions) and String.
     */
    public ParamDef<?> createParam(Object instance, ReflectionParameter param) {
        final Class<?> parameterType = param.getParameterType();

        if (parameterType == String.class) {
            final StringParam annotation = param.getAnnotation(StringParam.class).getOrElse(ParamDefaultAnnotations.defaultStringParam());
            return createStringParamDef(instance, param, annotation);
        }

        if (parameterType == Boolean.class || parameterType == Boolean.TYPE) {
            final BoolParam annotation = param.getAnnotation(BoolParam.class).getOrElse(ParamDefaultAnnotations.defaultBoolParam());
            return createBooleanParamDef(param, annotation);
        }

        if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            final IntParam annotation = param.getAnnotation(IntParam.class).getOrElse(ParamDefaultAnnotations.defaultIntParam());
            return createIntParamDef(param, annotation);
        }

        if (parameterType == Double.class || parameterType == Double.TYPE) {
            final DoubleParam annotation = param.getAnnotation(DoubleParam.class).getOrElse(ParamDefaultAnnotations.defaultDoubleParam());
            return createDoubleParamDef(param, annotation);
        }

        throw new IllegalArgumentException(String.format("Invalid param: index=%d, type=%s", param.getIndex(), parameterType));
    }

    private StringParamDef createStringParamDef(Object instance, ReflectionParameter param, StringParam annotation) {
        final StringParamDef.Builder builder = new StringParamDef.Builder(getOrGenerateName(annotation.value(), param));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            builder.setOptional(annotation.defaultValue());
        }

        // If the supplier name is not empty, use it as the values supplier.
        // Otherwise, use the values supplied by 'accepts'.
        final Opt<String> valuesSupplierName = getNonEmptyString(annotation.supplier());
        if (valuesSupplierName.isPresent()) {
            builder.setDynamicValues(ReflectionValuesSupplier.of(instance, valuesSupplierName.get()));
        } else {
            builder.setStaticValues(annotation.accepts());
        }

        return builder.build();
    }

    private BooleanParamDef createBooleanParamDef(ReflectionParameter param, BoolParam annotation) {
        final BooleanParamDef.Builder builder = new BooleanParamDef.Builder(getOrGenerateName(annotation.value(), param));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            builder.setOptional(annotation.defaultValue());
        }

        return builder.build();
    }

    private IntParamDef createIntParamDef(ReflectionParameter param, IntParam annotation) {
        final IntParamDef.Builder builder = new IntParamDef.Builder(getOrGenerateName(annotation.value(), param));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            builder.setOptional(annotation.defaultValue());
        }

        return builder.build();
    }

    private DoubleParamDef createDoubleParamDef(ReflectionParameter param, DoubleParam annotation) {
        final DoubleParamDef.Builder builder = new DoubleParamDef.Builder(getOrGenerateName(annotation.value(), param));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            builder.setOptional(annotation.defaultValue());
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

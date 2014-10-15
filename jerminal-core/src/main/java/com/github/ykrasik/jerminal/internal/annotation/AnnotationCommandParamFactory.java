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

/**
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class AnnotationCommandParamFactory {
    public static CommandParam createStringParam(StringParam annotation, int index) {
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

    public static CommandParam createFlagParam(FlagParam annotation, int index) {
        final String name = getOrGenerateName(annotation != null ? annotation.value() : "", "flag", index);
        final FlagParamBuilder builder = new FlagParamBuilder(name);

        final String description = annotation != null ? annotation.description() : "";
        if (!description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        return builder.build();
    }

    public static CommandParam createBooleanParam(BoolParam annotation, int index) {
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

    public static CommandParam createIntParam(IntParam annotation, int index) {
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

    public static CommandParam createDoubleParam(DoubleParam annotation, int index) {
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

    private static String getOrGenerateName(String name, String prefix, int index) {
        final String trimmedName = name.trim();
        if (!trimmedName.isEmpty()) {
            return trimmedName;
        } else {
            return prefix + "Param" + index;
        }
    }
}

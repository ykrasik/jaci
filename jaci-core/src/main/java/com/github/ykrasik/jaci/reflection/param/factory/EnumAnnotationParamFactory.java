/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 * *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.reflection.param.factory;

import com.github.ykrasik.jaci.api.EnumParam;
import com.github.ykrasik.jaci.param.EnumParamDef;
import com.github.ykrasik.jaci.reflection.ReflectionSuppliers;
import com.github.ykrasik.jaci.util.opt.Opt;

import static com.github.ykrasik.jaci.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link EnumParamDef}s out of {@link Enum} parameters annotated with {@link EnumParam}.
 * Empty names will be replaced with a generated name, and empty descriptions will use default values.
 *
 * @author Yevgeny Krasik
 */
public class EnumAnnotationParamFactory<E extends Enum<E>> extends AnnotationMethodParamFactory<EnumParamDef<E>, EnumParam> {
    public EnumAnnotationParamFactory() {
        super(EnumParam.class, Enum.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected EnumParamDef<E> createFromAnnotation(Object instance,
                                                   String defaultParamName,
                                                   EnumParam annotation,
                                                   Class<?> type) throws Exception {
        return doCreateFromAnnotation(instance, defaultParamName, annotation, (Class<E>) type);
    }

    private EnumParamDef<E> doCreateFromAnnotation(Object instance,
                                                   String defaultParamName,
                                                   EnumParam annotation,
                                                   Class<E> enumClass) throws Exception {
        final EnumParamDef.Builder<E> builder = new EnumParamDef.Builder<>(enumClass, getNonEmptyString(annotation.value()).getOrElse(defaultParamName));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            // If the defaultValueSupplier name is not empty, use it.
            // Otherwise, use the value supplied by 'defaultValue'.
            final Opt<String> defaultValueSupplierName = getNonEmptyString(annotation.defaultValueSupplier());
            if (defaultValueSupplierName.isPresent()) {
                builder.setOptional(ReflectionSuppliers.reflectionSupplier(instance, defaultValueSupplierName.get(), enumClass));
            } else {
                builder.setOptional(Enum.valueOf(enumClass, annotation.defaultValue()));
            }
        }

        builder.setNullable(annotation.nullable());

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected EnumParamDef<E> createDefault(String defaultParamName, Class<?> type) throws Exception {
        return new EnumParamDef.Builder<>((Class<E>) type, defaultParamName).build();
    }
}

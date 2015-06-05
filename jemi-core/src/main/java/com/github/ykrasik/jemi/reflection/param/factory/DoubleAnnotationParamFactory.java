/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.reflection.param.factory;

import com.github.ykrasik.jemi.api.DoubleParam;
import com.github.ykrasik.jemi.param.DoubleParamDef;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.ToString;

import static com.github.ykrasik.jemi.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link DoubleParamDef}s out of {@link Double} or {@code double} parameters annotated with {@link DoubleParam}.
 * Empty names will be replaced with a generated name, and empty descriptions will use default values.
 *
 * @author Yevgeny Krasik
 */
@ToString
public class DoubleAnnotationParamFactory extends AnnotationMethodParamFactory<DoubleParamDef, DoubleParam> {
    public DoubleAnnotationParamFactory() {
        super(DoubleParam.class, Double.class, Double.TYPE);
    }

    @Override
    protected DoubleParamDef createFromAnnotation(Object instance, String defaultParamName, DoubleParam annotation) throws Exception {
        final DoubleParamDef.Builder builder = new DoubleParamDef.Builder(getNonEmptyString(annotation.value()).getOrElse(defaultParamName));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            // If the defaultValueSupplier name is not empty, use it.
            // Otherwise, use the value supplied by 'defaultValue'.
            final Opt<String> defaultValueSupplierName = getNonEmptyString(annotation.defaultValueSupplier());
            if (defaultValueSupplierName.isPresent()) {
                // FIXME: Test that this works when the supplier returns primitive (Double.TYPE)
                builder.setOptional(Suppliers.reflectionSupplier(instance, defaultValueSupplierName.get(), Double.TYPE, Double.class));
            } else {
                builder.setOptional(annotation.defaultValue());
            }
        }

        return builder.build();
    }

    @Override
    protected DoubleParamDef createDefault(String defaultParamName) throws Exception {
        return new DoubleParamDef.Builder(defaultParamName).build();
    }
}

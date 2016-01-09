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

package com.github.ykrasik.jaci.reflection.param.factory;

import com.github.ykrasik.jaci.api.StringParam;
import com.github.ykrasik.jaci.param.StringParamDef;
import com.github.ykrasik.jaci.util.function.MoreSuppliers;
import com.github.ykrasik.jaci.util.opt.Opt;

import static com.github.ykrasik.jaci.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link StringParamDef}s out of {@link String} parameters annotated with {@link StringParam}.
 * Empty names will be replaced with a generated name, and empty descriptions will use default values.
 *
 * @author Yevgeny Krasik
 */
public class StringAnnotationParamFactory extends AnnotationMethodParamFactory<StringParamDef, StringParam> {
    public StringAnnotationParamFactory() {
        super(StringParam.class, String.class);
    }

    @Override
    protected StringParamDef createFromAnnotation(Object instance, String defaultParamName, StringParam annotation) throws Exception {
        final StringParamDef.Builder builder = new StringParamDef.Builder(getNonEmptyString(annotation.value()).getOrElse(defaultParamName));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            // If the defaultValueSupplier name is not empty, use it.
            // Otherwise, use the value supplied by 'defaultValue'.
            final Opt<String> defaultValueSupplierName = getNonEmptyString(annotation.defaultValueSupplier());
            if (defaultValueSupplierName.isPresent()) {
                builder.setOptional(MoreSuppliers.reflectionSupplier(instance, defaultValueSupplierName.get(), String.class));
            } else {
                builder.setOptional(annotation.defaultValue());
            }
        }

        // If the supplier name is not empty, use it as the values supplier.
        // Otherwise, use the values supplied by 'accepts'.
        final Opt<String> valuesSupplierName = getNonEmptyString(annotation.acceptsSupplier());
        if (valuesSupplierName.isPresent()) {
            builder.setDynamicValues(MoreSuppliers.reflectionListSupplier(instance, valuesSupplierName.get(), String[].class));
        } else {
            builder.setStaticValues(annotation.accepts());
        }

        return builder.build();
    }

    @Override
    protected StringParamDef createDefault(String defaultParamName) throws Exception {
        return new StringParamDef.Builder(defaultParamName).build();
    }
}

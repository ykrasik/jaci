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

import com.github.ykrasik.jaci.api.BoolParam;
import com.github.ykrasik.jaci.param.BooleanParamDef;
import com.github.ykrasik.jaci.reflection.ReflectionSuppliers;
import com.github.ykrasik.jaci.util.opt.Opt;

import static com.github.ykrasik.jaci.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link BooleanParamDef}s out of {@link Boolean} or {@code boolean} parameters annotated with {@link BoolParam}.
 * Empty names will be replaced with a generated name, and empty descriptions will use default values.
 *
 * @author Yevgeny Krasik
 */
public class BooleanAnnotationParamFactory extends AnnotationMethodParamFactory<BooleanParamDef, BoolParam> {
    public BooleanAnnotationParamFactory() {
        super(BoolParam.class, Boolean.class, Boolean.TYPE);
    }

    @Override
    protected BooleanParamDef createFromAnnotation(Object instance, String defaultParamName, BoolParam annotation) throws Exception {
        final BooleanParamDef.Builder builder = new BooleanParamDef.Builder(getNonEmptyString(annotation.value()).getOrElse(defaultParamName));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        if (annotation.optional()) {
            // If the defaultValueSupplier name is not empty, use it.
            // Otherwise, use the value supplied by 'defaultValue'.
            final Opt<String> defaultValueSupplierName = getNonEmptyString(annotation.defaultValueSupplier());
            if (defaultValueSupplierName.isPresent()) {
                // FIXME: Test that this works when the supplier returns primitive (Boolean.TYPE)
                builder.setOptional(ReflectionSuppliers.reflectionSupplier(instance, defaultValueSupplierName.get(), Boolean.TYPE, Boolean.class));
            } else {
                builder.setOptional(annotation.defaultValue());
            }
        }

        return builder.build();
    }

    @Override
    protected BooleanParamDef createDefault(String defaultParamName) throws Exception {
        return new BooleanParamDef.Builder(defaultParamName).build();
    }
}

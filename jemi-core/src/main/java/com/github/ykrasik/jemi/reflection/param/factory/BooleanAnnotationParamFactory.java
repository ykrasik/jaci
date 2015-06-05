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

import com.github.ykrasik.jemi.api.BoolParam;
import com.github.ykrasik.jemi.param.BooleanParamDef;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.reflection.ReflectionParameter;
import lombok.ToString;

import static com.github.ykrasik.jemi.util.string.StringUtils.getNonEmptyString;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@ToString
public class BooleanAnnotationParamFactory extends AnnotationMethodParamFactory<BooleanParamDef, BoolParam> {
    public BooleanAnnotationParamFactory() {
        super(BoolParam.class, Boolean.class, Boolean.TYPE);
    }

    @Override
    protected BooleanParamDef createWithAnnotation(Object instance, ReflectionParameter param, BoolParam annotation) throws Exception {
        final BooleanParamDef.Builder builder = new BooleanParamDef.Builder(getNonEmptyString(annotation.value()).getOrElse(param.getDefaultName()));

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
                builder.setOptional(Suppliers.reflectionSupplier(instance, defaultValueSupplierName.get(), Boolean.class));
            } else {
                builder.setOptional(annotation.defaultValue());
            }
        }

        return builder.build();
    }

    @Override
    protected BooleanParamDef createWithoutAnnotation(Object instance, ReflectionParameter param) throws Exception {
        return new BooleanParamDef.Builder(param.getDefaultName()).build();
    }
}

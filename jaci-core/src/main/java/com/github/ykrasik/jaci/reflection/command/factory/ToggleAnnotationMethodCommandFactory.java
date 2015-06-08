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

package com.github.ykrasik.jaci.reflection.command.factory;

import com.github.ykrasik.jaci.api.ToggleCommand;
import com.github.ykrasik.jaci.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.reflection.command.ToggleCommandDefBuilder;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.reflection.ReflectionUtils;

import java.lang.reflect.Method;

import static com.github.ykrasik.jaci.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link CommandDef}s out of {@link Method}s annotated with {@link ToggleCommand}.
 * Empty names will be replaced with a the method's name, and empty descriptions will use default values.<br>
 *
 * @author Yevgeny Krasik
 */
public class ToggleAnnotationMethodCommandFactory extends AbstractAnnotationMethodCommandFactory<ToggleCommand> {
    public ToggleAnnotationMethodCommandFactory() {
        super(ToggleCommand.class);
    }

    @Override
    protected CommandDef doCreate(Object instance, Method method, ToggleCommand annotation) throws Exception {
        ReflectionUtils.assertReturnValue(method, ToggleCommandStateAccessor.class);
        ReflectionUtils.assertNoParameters(method);
        final ToggleCommandStateAccessor accessor = ReflectionUtils.invokeNoArgs(instance, method);

        final String name = getNonEmptyString(annotation.value()).getOrElse(method.getName());
        final ToggleCommandDefBuilder builder = new ToggleCommandDefBuilder(name, accessor);

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
}

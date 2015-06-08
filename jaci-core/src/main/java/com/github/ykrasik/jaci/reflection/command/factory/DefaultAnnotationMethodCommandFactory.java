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

import com.github.ykrasik.jaci.api.Command;
import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.param.ParamDef;
import com.github.ykrasik.jaci.reflection.command.ReflectionCommandExecutor;
import com.github.ykrasik.jaci.reflection.param.ReflectionParamProcessor;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.reflection.ReflectionParameter;
import com.github.ykrasik.jaci.util.reflection.ReflectionUtils;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;

import static com.github.ykrasik.jaci.util.string.StringUtils.getNonEmptyString;

/**
 * Creates {@link CommandDef}s out of {@link Method}s annotated with {@link Command}.
 * Empty names will be replaced with a the method's name, and empty descriptions will use default values.<br>
 *
 * @author Yevgeny Krasik
 */
public class DefaultAnnotationMethodCommandFactory extends AbstractAnnotationMethodCommandFactory<Command> {
    private final ReflectionParamProcessor paramProcessor;

    public DefaultAnnotationMethodCommandFactory() {
        this(new ReflectionParamProcessor());
    }

    /**
     * Package-visible for testing.
     */
    DefaultAnnotationMethodCommandFactory(@NonNull ReflectionParamProcessor paramProcessor) {
        super(Command.class);
        this.paramProcessor = paramProcessor;
    }

    @Override
    protected CommandDef doCreate(Object instance, Method method, Command annotation) throws Exception {
        // Reflect method params.
        final List<ReflectionParameter> params = ReflectionUtils.reflectMethodParameters(method);

        // First parameter must be CommandOutput.
        if (params.isEmpty() || !isCommandOutput(params.get(0).getParameterType())) {
            throw new IllegalArgumentException("First parameter of a command must be of type " + CommandOutput.class);
        }

        final String name = getNonEmptyString(annotation.value()).getOrElse(method.getName());
        final CommandDef.Builder builder = new CommandDef.Builder(name, new ReflectionCommandExecutor(instance, method));

        final Opt<String> description = getNonEmptyString(annotation.description());
        if (description.isPresent()) {
            builder.setDescription(description.get());
        }

        for (int i = 1; i < params.size(); i++) {
            final ReflectionParameter param = params.get(i);
            final ParamDef<?> paramDef = paramProcessor.createParam(instance, param);
            builder.addParam(paramDef);
        }

        return builder.build();
    }

    private boolean isCommandOutput(Class<?> clazz) {
        return CommandOutput.class.isAssignableFrom(clazz);
    }
}

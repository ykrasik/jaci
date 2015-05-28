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

package com.github.ykrasik.jemi.cli.command;

import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.cli.param.CliParamResolver;
import com.github.ykrasik.jemi.core.Identifiable;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.core.command.CommandDef;
import com.github.ykrasik.jemi.core.param.ParamDef;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor
public class CliCommand implements Identifiable {
    @Getter
    @NonNull private final Identifier identifier;

    @Getter
    @NonNull private final List<CliParam> params;

    @Delegate
    @NonNull private final CliCommandExecutor executor;

    // TODO: JavaDoc
    public String getName() {
        // Implemented manually due to bugs with IntelliJ compatibility.
        return identifier.getName();
    }

    // TODO: JavaDoc
    public String getDescription() {
        // Implemented manually due to bugs with IntelliJ compatibility.
        return identifier.getName();
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    public static CliCommand fromDef(@NonNull CommandDef def) {
        final List<CliParam> params = createParams(def.getParamDefs());
        return new CliCommand(def.getIdentifier(), params, new CliCommandExecutorWrapper(def.getExecutor()));
    }

    private static List<CliParam> createParams(List<ParamDef<?>> paramDefs) {
        final List<CliParam> params = new ArrayList<>(paramDefs.size());
        for (ParamDef<?> def : paramDefs) {
            params.add(def.resolve(RESOLVER));
        }
        return params;
    }

    private static final CliParamResolver RESOLVER = new CliParamResolver();
}

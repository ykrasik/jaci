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

package com.github.ykrasik.jaci.cli.command;

import com.github.ykrasik.jaci.Identifiable;
import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.cli.param.CliParam;
import com.github.ykrasik.jaci.cli.param.CliParamManager;
import com.github.ykrasik.jaci.cli.param.CliParamManagerImpl;
import com.github.ykrasik.jaci.cli.param.CliParamResolver;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.command.CommandExecutor;
import com.github.ykrasik.jaci.param.ParamDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A CLI implementation of a command.
 *
 * @author Yevgeny Krasik
 */
public class CliCommand implements Identifiable, CliParamManager, CommandExecutor {
    private final Identifier identifier;
    private final CliParamManager paramManager;
    private final CommandExecutor executor;

    private CliCommand(Identifier identifier, CliParamManager paramManager, CommandExecutor executor) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        this.paramManager = Objects.requireNonNull(paramManager, "paramManager");
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * @return Command name.
     */
    public String getName() {
        return identifier.getName();
    }

    /**
     * @return Command description.
     */
    public String getDescription() {
        return identifier.getDescription();
    }

    @Override
    public List<CliParam> getParams() {
        return paramManager.getParams();
    }

    @Override
    public CommandArgs parse(List<String> args) throws ParseException {
        return paramManager.parse(args);
    }

    @Override
    public ParamAssistInfo assist(List<String> args) throws ParseException {
        return paramManager.assist(args);
    }

    @Override
    public void execute(CommandOutput output, CommandArgs args) throws Exception {
        executor.execute(output, args);
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    /**
     * Construct a CLI command from a {@link CommandDef}.
     *
     * @param def CommandDef to construct a CLI command from.
     * @return A CLI command constructed from the CommandDef.
     */
    public static CliCommand fromDef(CommandDef def) {
        final Identifier identifier = def.getIdentifier();
        final List<CliParam> params = createParams(def.getParamDefs());
        final CommandExecutor executor = def.getExecutor();
        return from(identifier, params, executor);
    }

    /**
     * Construct a CLI command from the given parameters.
     *
     * @param identifier Command identifier.
     * @param params CLI parameters to use.
     * @param executor Command executor.
     * @return A CLI command constructed from the given parameters.
     */
    public static CliCommand from(Identifier identifier, List<CliParam> params, CommandExecutor executor) {
        final CliParamManager paramManager = new CliParamManagerImpl(params);
        return new CliCommand(identifier, paramManager, executor);
    }

    private static final CliParamResolver RESOLVER = new CliParamResolver();

    private static List<CliParam> createParams(List<ParamDef<?>> paramDefs) {
        final List<CliParam> params = new ArrayList<>(paramDefs.size());
        for (ParamDef<?> def : paramDefs) {
            params.add(def.resolve(RESOLVER));
        }
        return params;
    }
}

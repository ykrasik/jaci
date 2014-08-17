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

package com.github.ykrasik.jerminal.api.command;

import com.github.ykrasik.jerminal.internal.filesystem.command.ShellCommandImpl;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A builder for a {@link ShellCommand}.
 *
 * @author Yevgeny Krasik
 */
public class ShellCommandBuilder {
    private static final CommandExecutor NOT_IMPLEMENTED_EXECUTOR = new CommandExecutor() {
        @Override
        public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
            throw new ExecuteException("Not implemented!");
        }
    };

    private final String name;
    private String description = "command";
    private final List<CommandParam> params = new ArrayList<>(4);
    private CommandExecutor executor = NOT_IMPLEMENTED_EXECUTOR;

    public ShellCommandBuilder(String name) {
        this.name = name;
    }

    public ShellCommand build() {
        return new ShellCommandImpl(name, description, params, executor);
    }

    public ShellCommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ShellCommandBuilder addParam(CommandParam param) {
        this.params.add(param);
        return this;
    }

    public ShellCommandBuilder addParams(CommandParam... params) {
        return addParams(Arrays.asList(params));
    }

    public ShellCommandBuilder addParams(List<CommandParam> params) {
        this.params.addAll(params);
        return this;
    }

    public ShellCommandBuilder setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }
}

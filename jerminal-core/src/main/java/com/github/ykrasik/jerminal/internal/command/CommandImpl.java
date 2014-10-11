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

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation for a {@link Command}.
 *
 * @author Yevgeny Krasik
 */
public class CommandImpl extends AbstractDescribable implements Command {
    private final CommandExecutor executor;
    private final List<CommandParam> params;

    public CommandImpl(String name,
                       String description,
                       List<CommandParam> params,
                       CommandExecutor executor) {
        super(name, description);

        this.executor = checkNotNull(executor, "executor");
        this.params = Collections.unmodifiableList(checkNotNull(params, "params"));
    }

    @Override
    public List<CommandParam> getParams() {
        return params;
    }

    @Override
    public void execute(CommandArgs args, OutputPrinter outputPrinter) throws Exception {
        executor.execute(args, outputPrinter);
    }
}

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

package com.github.ykrasik.jerminal.internal.filesystem.command;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.command.parameter.CommandParamContext;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.returnvalue.AssistReturnValue;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation for a {@link ShellCommand}.
 *
 * @author Yevgeny Krasik
 */
public class ShellCommandImpl extends AbstractDescribable implements ShellCommand {
    public static final char ARG_VALUE_DELIMITER = '=';

    private final CommandExecutor executor;
    private final List<CommandParam> positionalParams;
    private final Trie<CommandParam> params;

    public ShellCommandImpl(String name,
                            String description,
                            List<CommandParam> params,
                            CommandExecutor executor) {
        super(name, description);

        this.executor = checkNotNull(executor, "executor");
        this.positionalParams = Collections.unmodifiableList(checkNotNull(params, "params"));
        this.params = createParamTrie(params);
    }

    private Trie<CommandParam> createParamTrie(List<CommandParam> params) {
        final TrieBuilder<CommandParam> builder = new TrieBuilder<>();
        for (CommandParam param : params) {
            final String paramName = param.getName();
            if (!isLegalName(paramName)) {
                throw new ShellException("Illegal param name: '%s'. Param names cannot contain '%c'!", paramName, ARG_VALUE_DELIMITER);
            }

            builder.add(paramName, param);
        }
        return builder.build();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public List<CommandParam> getParams() {
        return positionalParams;
    }

    @Override
    public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
        executor.execute(args, outputPrinter);
    }

    @Override
    public CommandArgs parseCommandArgs(List<String> args) throws ParseException {
        final CommandParamContext context = new CommandParamContext(this, params);
        try {
            return context.parseCommandArgs(args);
        } catch (ParseException e) {
            // Add command info to the exception.
            final CommandInfo commandInfo = context.createCommandInfo();
            throw e.withCommandInfo(commandInfo);
        }
    }

    @Override
    public AssistReturnValue assistArgs(List<String> args) throws ParseException {
        final CommandParamContext context = new CommandParamContext(this, params);
        try {
            return context.assistArgs(args);
        } catch (ParseException e) {
            // Add command info to the exception.
            final CommandInfo commandInfo = context.createCommandInfo();
            throw e.withCommandInfo(commandInfo);
        }
    }

    private boolean isLegalName(String name) {
        return name.indexOf(ARG_VALUE_DELIMITER) == -1;
    }
}

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

package com.github.ykrasik.jerminal.internal.filesystem.file;

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.command.Command;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.github.ykrasik.jerminal.internal.command.parameter.CommandParamContext;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.returnvalue.AssistReturnValue;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An <b>immutable</b> implementation for a {@link ShellFile}.
 *
 * @author Yevgeny Krasik
 */
public class ShellFileImpl implements ShellFile {
    private final Command command;
    private final Trie<CommandParam> params;

    public ShellFileImpl(Command command) {
        this.command = checkNotNull(command, "command");
        this.params = createParamTrie(command.getParams());
    }

    private Trie<CommandParam> createParamTrie(List<CommandParam> params) {
        Trie<CommandParam> trie = new TrieImpl<>();
        for (CommandParam param : params) {
            final String name = param.getName();
            if (!ShellConstants.isLegalName(name)) {
                throw new ShellException("Illegal name for parameter: '%s'", name);
            }

            trie = trie.add(name, param);
        }
        return trie;
    }

    @Override
    public String getName() {
        return command.getName();
    }

    @Override
    public String getDescription() {
        return command.getDescription();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public CommandArgs parseCommandArgs(List<String> args) throws ParseException {
        final CommandParamContext context = new CommandParamContext(command, params);
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
        final CommandParamContext context = new CommandParamContext(command, params);
        try {
            return context.assistArgs(args);
        } catch (ParseException e) {
            // Add command info to the exception.
            final CommandInfo commandInfo = context.createCommandInfo();
            throw e.withCommandInfo(commandInfo);
        }
    }
}

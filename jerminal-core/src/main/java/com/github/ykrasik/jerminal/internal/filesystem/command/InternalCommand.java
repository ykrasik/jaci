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

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.github.ykrasik.jerminal.internal.Describable;
import com.github.ykrasik.jerminal.internal.command.parameter.CommandParamManager;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.assist.AssistReturnValue;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An internal representation of a {@link Command}.<br>
 * Can parse and auto complete the command's arguments.<br>
 * Any changes to the underlying {@link Command} will put this object in an <b>UNDEFINED STATE</b>.
 * Don't do this. Why would you do this, anyway?
 *
 * @author Yevgeny Krasik
 */
public class InternalCommand implements Describable {
    private final Command command;
    private final Trie<CommandParam> paramsTrie;

    public InternalCommand(Command command) {
        if (!ShellConstants.isValidName(command.getName())) {
            throw new ShellException("Invalid name for command: '%s'", command.getName());
        }

        this.command = Objects.requireNonNull(command);
        this.paramsTrie = createParamTrie(command.getParams());
    }

    private Trie<CommandParam> createParamTrie(List<CommandParam> params) {
        Trie<CommandParam> trie = new TrieImpl<>();
        for (CommandParam param : params) {
            final String name = param.getName();
            if (!ShellConstants.isValidName(name)) {
                throw new ShellException("Invalid name for parameter: '%s'", name);
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

    /**
     * @return The command wrapped by this object.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Parses arguments for the command.
     *
     * @param args Args to be parsed.
     * @return Parsed args for the command.
     * @throws ParseException If the one of the args is invalid or a mandatory parameter is missing.
     */
    public CommandArgs parseCommandArgs(List<String> args) throws ParseException {
        final CommandParamManager paramManager = new CommandParamManager(paramsTrie, command.getParams());
        try {
            return paramManager.parseCommandArgs(args);
        } catch (ParseException e) {
            // Add command info to the exception.
            final CommandInfo commandInfo = createCommandInfo(paramManager);
            throw e.withCommandInfo(commandInfo);
        }
    }

    /**
     * Offers assistance with the args. All args are expected to be validly parsable except the last one,
     * for which the assistance will be offered.
     *
     * @param args Args to be auto completed.
     * @return Assistance for the next available {@link CommandParam}.
     * @throws ParseException If the one of the args is invalid or a mandatory parameter is missing.
     */
    public AssistReturnValue assistArgs(List<String> args) throws ParseException {
        final CommandParamManager paramManager = new CommandParamManager(paramsTrie, command.getParams());
        try {
            final AutoCompleteReturnValue autoCompleteReturnValue = paramManager.autoCompleteLastArg(args);
            final CommandInfo commandInfo = createCommandInfo(paramManager);
            return new AssistReturnValue(Optional.of(commandInfo), autoCompleteReturnValue);
        } catch (ParseException e) {
            // Add command info to the exception.
            final CommandInfo commandInfo = createCommandInfo(paramManager);
            throw e.withCommandInfo(commandInfo);
        }
    }

    private CommandInfo createCommandInfo(CommandParamManager paramManager) {
        final List<Optional<String>> paramValues = createParamValues(paramManager);
        final Optional<CommandParam> currentParam = paramManager.getCurrentParam();
        return new CommandInfo(command, paramValues, currentParam);
    }

    private List<Optional<String>> createParamValues(CommandParamManager paramManager) {
        final List<CommandParam> params = command.getParams();
        final List<Optional<String>> paramValues = new ArrayList<>(params.size());
        for (CommandParam param : params) {
            final Optional<String> value = paramManager.getParamRawValue(param.getName());
            paramValues.add(value);
        }
        return paramValues;
    }

    @Override
    public String toString() {
        return command.toString();
    }
}

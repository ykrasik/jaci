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

package com.github.ykrasik.jerminal.api.display.terminal;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;

/**
 * Serializes objects into {@link String}.
 *
 * @author Yevgeny Krasik
 */
public interface TerminalSerializer {
    /**
     * @param commandInfo {@link CommandInfo} to serialize.
     * @return Serialized {@link CommandInfo}.
     */
    String serializeCommandInfo(CommandInfo commandInfo);

    /**
     * @param suggestions {@link Suggestions} to serialize.
     * @return Serialized {@link Suggestions}.
     */
    String serializeSuggestions(Suggestions suggestions);

    /**
     * @param directory {@link ShellDirectory} to serialize.
     * @return Serialized {@link ShellDirectory}.
     */
    String serializeDirectory(ShellDirectory directory);

    /**
     * @param command {@link Command} to serialize.
     * @return Serialized {@link Command}.
     */
    String serializeCommand(Command command);

    /**
     * @param e {@link Exception} to serialize.
     * @return Serialized {@link Exception}.
     */
    String serializeException(Exception e);
}
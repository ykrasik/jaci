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

package com.github.ykrasik.jerminal.api.output.terminal;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;

/**
 * Translates objects into {@link String Strings}, to be printed on a {@link Terminal}.
 *
 * @author Yevgeny Krasik
 */
public interface TerminalSerializer {
    /**
     * Returns a {@link String} representing an empty line.
     */
    String getEmptyLine();

    /**
     * Serializes an {@link CommandInfo} into a {@link String}.
     */
    String serializeCommandInfo(CommandInfo commandInfo);

    /**
     * Serializes a {@link Suggestions} into a {@link String}.
     */
    String serializeSuggestions(Suggestions suggestions);

    /**
     * Serializes a {@link ShellEntryView} into a {@link String}.
     */
    String serializeShellEntryView(ShellEntryView shellEntryView);

    /**
     * Serializes a {@link ShellCommandView} into a {@link String}.
     */
    String serializeShellCommandView(ShellCommandView shellCommandView);

    /**
     * Serializes an {@link Exception} into a {@link String}.
     */
    String serializeException(Exception e);
}

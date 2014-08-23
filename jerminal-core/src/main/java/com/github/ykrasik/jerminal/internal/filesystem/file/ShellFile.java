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

import com.github.ykrasik.jerminal.api.command.Command;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.github.ykrasik.jerminal.internal.returnvalue.AssistReturnValue;

import java.util.List;

/**
 * A container for a single {@link Command}.<br>
 * Also in charge of parsing and assisting with the {@link Command}'s args.
 *
 * @author Yevgeny Krasik
 */
public interface ShellFile extends ShellEntry {
    /**
     * Returns contained the {@link Command}.
     */
    Command getCommand();

    /**
     * Parse the args according to the command's expected parameters.
     *
     * @throws ParseException If the one of the args is invalid or a mandatory parameter is missing.
     */
    CommandArgs parseCommandArgs(List<String> args) throws ParseException;

    /**
     * Offer assistance for the next available {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam parameter}.
     *
     * @throws ParseException If the one of the args is invalid or a mandatory parameter is missing.
     */
    AssistReturnValue assistArgs(List<String> args) throws ParseException;
}

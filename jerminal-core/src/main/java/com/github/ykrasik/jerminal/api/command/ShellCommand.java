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

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;

import java.util.List;

/**
 * Can execute code according to parameters that it receives.<br>
 *
 * <p>Parameter values can be passed in 2 ways: positional and named.<br>
 * <i>Positional:</i> The value is parsed by the next {@link CommandParam parameter} that is still unparsed.<br>
 * <i>Named:</i> The value is expected to be of the form "{name}={value}" and will be parsed
 *               by the {@link CommandParam parameter} who's name is "{name}".</p>
 *
 * @author Yevgeny Krasik
 */
// TODO: Consider hiding this class a bit better, it's in an API package after all.
// TODO: Create a ShellFile class which will contain this one, and rename this to Command.
public interface ShellCommand extends ShellEntry {
    /**
     * Returns the command's declared parameters.
     */
    List<CommandParam> getParams();

    /**
     * Parse the args according to the command's expected parameters.
     * @throws ParseException If the one of the args is invalid or a mandatory parameter is missing.
     */
    CommandArgs parseCommandArgs(List<String> args) throws ParseException;

    /**
     * Offer auto complete suggestions for the next available {@link CommandParam parameter}.
     * @throws ParseException If the one of the args is invalid or a mandatory parameter is missing.
     */
    AutoCompleteReturnValue autoCompleteArgs(List<String> args) throws ParseException;

    /**
     * Execute with the given arguments.<br>
     * Output can be written to the supplied {@link OutputPrinter}.
     * @throws ExecuteException If an error occurs during execution.
     */
    void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException;
}

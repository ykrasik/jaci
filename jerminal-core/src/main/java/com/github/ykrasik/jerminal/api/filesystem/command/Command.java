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

package com.github.ykrasik.jerminal.api.filesystem.command;

import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.Describable;

import java.util.List;

/**
 * A definition for a command.<br>
 * Defines the command parameters in the order they appear and can execute code given arguments for those parameters.
 * <br>
 * <p>Parameter values can be passed in 2 ways: positional and named.<br>
 * <i>Positional:</i> The value is parsed by the next {@link CommandParam parameter} that is still unparsed.<br>
 * <i>Named:</i> The value is expected to be of the form "{name}={value}" and will be parsed
 * by the {@link CommandParam} who's name is "{name}".</p>
 *
 * @author Yevgeny Krasik
 */
public interface Command extends Describable {
    /**
     * @return The command's declared parameters.
     */
    List<CommandParam> getParams();

    /**
     * Execute with the given arguments.<br>
     * Output can be written to the supplied {@link com.github.ykrasik.jerminal.api.command.OutputPrinter}.
     * May throw an {@link com.github.ykrasik.jerminal.api.exception.ExecuteException ExecuteException}
     * in case of an invalid internal state. This will be considered an execution error. Any other exception
     * thrown will be considered as an unhandled exception.
     *
     * @param args Parsed args. This object can be queried for arg values.
     * @param outputPrinter Used to print text onto the display.
     * @throws Exception If an error occurs during execution.
     */
    void execute(CommandArgs args, OutputPrinter outputPrinter) throws Exception;
}

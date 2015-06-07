/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.param;

import com.github.ykrasik.jemi.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.command.CommandArgs;

import java.util.List;

/**
 * A component that is in charge of parsing arguments and providing assistance for parameters.
 *
 * @author Yevgeny Krasik
 */
public interface CliParamManager {
    /**
     * @return The parameters.
     */
    List<CliParam> getParams();

    /**
     * Parse the given arguments.
     *
     * @param args Arguments to be parsed.
     * @return Parsed arguments.
     * @throws ParseException If any error occurs while parsing the arguments.
     */
    CommandArgs parse(List<String> args) throws ParseException;

    /**
     * Provide assistance for about the parameters for the given arguments.
     * Only the last argument is up for auto-completion, the rest are expected to be valid parameter values.
     *
     * @param args Arguments to be auto-completed. Will only auto-complete the last arg.
     * @return Auto complete suggestions for the last arg. Every preceding arg is expected to be a valid param value.
     * @throws ParseException If any of the args except the last one can't be validly parsed.
     */
    ParamAssistInfo assist(List<String> args) throws ParseException;
}

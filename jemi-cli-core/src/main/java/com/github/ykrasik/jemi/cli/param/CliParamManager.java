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
import com.github.ykrasik.jemi.core.command.CommandArgs;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface CliParamManager {
    // TODO: JavaDoc
    List<CliParam> getParams();

    /**
     * @param args Args to be parsed.
     * @return Parsed args.
     * @throws ParseException If an invalid value was supplied for a param, or if a param wasn't bound.
     */
    CommandArgs parse(List<String> args) throws ParseException;

    /**
     * @param args Args to be auto-completed. Will only auto-complete the last arg.
     * @return Auto complete suggestions for the last arg. Every preceding arg is expected to be a valid param value.
     * @throws ParseException If any of the args except the last one can't be validly parsed.
     */
    // TODO: JavaDoc
    ParamAssistInfo assist(List<String> args) throws ParseException;
}

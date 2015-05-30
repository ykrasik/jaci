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

package com.github.ykrasik.jemi.cli.output;

import com.github.ykrasik.jemi.cli.assist.CommandInfo;
import com.github.ykrasik.jemi.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jemi.cli.assist.Suggestions;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface CliSerializer {
    // TODO: JavaDoc
    String serializePathToDirectory(CliDirectory directory);

    // TODO: JavaDoc
    String serializeCommandLine(String commandLine);

    // TODO: JavaDoc
    List<String> serializeDirectory(CliDirectory directory, boolean recursive);

    // TODO: JavaDoc
    List<String> serializeCommand(CliCommand command);

    // TODO: JavaDoc
    List<String> serializeException(Exception e);

    // TODO: JavaDoc
    List<String> serializeCommandInfo(CommandInfo info);

    // TODO: JavaDoc
    List<String> serializeSuggestions(Suggestions suggestions);
}

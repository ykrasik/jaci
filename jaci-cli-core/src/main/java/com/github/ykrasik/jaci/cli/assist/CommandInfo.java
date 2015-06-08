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

package com.github.ykrasik.jaci.cli.assist;

import com.github.ykrasik.jaci.cli.command.CliCommand;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
// FIXME: This class may be redundant... Either actually attach it to ParseExceptions or remove it.
@Data
public class CommandInfo {
    @NonNull private final CliCommand command;
    @NonNull private final BoundParams boundParams;
}

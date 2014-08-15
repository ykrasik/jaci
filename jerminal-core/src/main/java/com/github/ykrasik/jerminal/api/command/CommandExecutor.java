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

import com.rawcod.jerminal.exception.ExecuteException;

/**
 * Executes code according to given arguments.
 *
 * @author Yevgeny Krasik
 */
public interface CommandExecutor {
    /**
     * Executes code according to given arguments.
     * Output can be written to the supplied {@link OutputBuffer}.
     * @throws ExecuteException If an error occurs during execution.
     */
    void execute(CommandArgs args, OutputBuffer output) throws ExecuteException;
}

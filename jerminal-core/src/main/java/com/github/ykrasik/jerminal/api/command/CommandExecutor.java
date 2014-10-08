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

import com.github.ykrasik.jerminal.api.exception.ExecuteException;

/**
 * Executes code according to given arguments.
 *
 * @author Yevgeny Krasik
 */
public interface CommandExecutor {
    /**
     * Executes code according to given arguments.<br>
     * Output can be written to the supplied {@link OutputPrinter}.<br>
     * May throw an {@link com.github.ykrasik.jerminal.api.exception.ExecuteException ExecuteException}
     * in case of an invalid internal state. This will be considered an execution error. Any other exception
     * thrown will be considered as an unhandled exception.
     *
     * @throws Exception If an error occurs during execution.
     */
    void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException, Exception;
}

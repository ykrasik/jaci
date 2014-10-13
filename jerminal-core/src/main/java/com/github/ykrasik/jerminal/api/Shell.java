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

package com.github.ykrasik.jerminal.api;

import com.google.common.base.Optional;

/**
 * Processes command lines and displays results as a side effect through a {@link com.github.ykrasik.jerminal.api.display.DisplayDriver}.<br>
 * <br>
 * The Shell does not alter the command line in any way. It is assumed that an external system
 * has ownership of the command line and is the one in charge of manipulating it. The Shell, in turn, simply returns
 * what the new command line should be on each of it's calls.<br>
 * <br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: Remove Guava dependency?
public interface Shell {
    // TODO: Create AssistReturnValue and ExecuteReturnValue instead of having a DisplayDriver with side effects?
    /**
     * Provide assistance for the command line.
     *
     * @param commandLine Command line to provide assistance for.
     * @return The new command line if assistance was possible.
     */
    Optional<String> assist(String commandLine);

    /**
     * Execute the command line.
     *
     * @param commandLine Command line to execute.
     * @return True if the command line was executed successfully.
     */
    boolean execute(String commandLine);
}
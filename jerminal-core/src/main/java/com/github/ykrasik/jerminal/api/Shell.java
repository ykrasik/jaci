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
 * Processes command lines and displays results through an {@link com.github.ykrasik.jerminal.api.output.OutputProcessor OutputProcessor}.<br>
 * <br>
 * The Shell keeps a command history. Only successfully executed command lines are saved to the history.
 * The Shell keeps track of where in the command history it is. So the state is kept inside the Shell for consecutive
 * calls to {@link #getPrevCommandLineFromHistory()} and {@link #getNextCommandLineFromHistory()}. Executing a new command line
 * will reset the command line history pointer and add the new command line to the end of the history.<br>
 * <br>
 * The Shell does not alter the command line in any way. It is assumed that an external system (let's call it a ShellDriver)
 * has ownership of the command line and is the one in charge of manipulating it. The Shell, in turn, simply returns
 * what the new command line should be on each of it's calls.<br>
 * <br>
 * Can be built through a {@link ShellBuilder}.
 *
 * @author Yevgeny Krasik
 */
public interface Shell {
    /**
     * Get the previous command line from history.<br>
     * Only successfully executed command lines are saved.<br>
     * The caller of this method is expected to alter the command line accordingly.
     */
    Optional<String> getPrevCommandLineFromHistory();

    /**
     * Get the next command line from history.<br>
     * Only successfully executed command lines are saved.<br>
     * The caller of this method is expected to alter the command line accordingly.
     */
    Optional<String> getNextCommandLineFromHistory();

    /**
     * Provide assistance according to the command line.<br>
     * Returns the new command line.<br>
     * The caller of this method is expected to alter the command line accordingly.
     */
    String assist(String commandLine);

    /**
     * Execute the command line.<br>
     * Returns the new command line. If the command line was executed successfully, returns an empty command line.
     * Otherwise, returns the received command line. <br>
     * The caller of this method is expected to alter the command line accordingly.
     */
    String execute(String commandLine);
}

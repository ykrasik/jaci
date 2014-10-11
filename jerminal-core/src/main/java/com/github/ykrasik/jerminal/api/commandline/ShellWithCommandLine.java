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

package com.github.ykrasik.jerminal.api.commandline;

/**
 * A similar but different API to a {@link com.github.ykrasik.jerminal.api.Shell Shell}.<br>
 * This assumes access to the command line - specifically the ability to read from and write to the
 * command line. Only returns indications of success, because the actual logic is performed as a side effect.
 *
 * @author Yevgeny Krasik
 */
public interface ShellWithCommandLine {
    /**
     * Clear the command line.
     */
    void clearCommandLine();

    /**
     * Set the command line to the previous one from history.
     * @return true if there was a previous command line in history.
     */
    boolean setPrevCommandLineFromHistory();

    /**
     * Set the command line to the next one from history.
     * @return true if there was a next command line in history.
     */
    boolean setNextCommandLineFromHistory();

    /**
     * Provide assistance for the command line.
     * @return true if assistance could be provided for the command line.
     */
    boolean assist();

    /**
     * Execute the command line.
     * @return true if the command line could be executed successfully.
     */
    boolean execute();
}

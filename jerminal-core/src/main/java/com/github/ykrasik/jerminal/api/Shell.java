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

/**
 * Analyzes the command line and acts accordingly.<br>
 * Does not directly return values, but sends commands to an {@link com.rawcod.jerminal.output.OutputProcessor OutputProcessor}.<br>
 * Can be built through a {@link ShellBuilder}.
 *
 * @author Yevgeny Krasik
 */
public interface Shell {
    /**
     * Clear the command line.
     */
    void clearCommandLine();

    /**
     * Set the command line to the previous one from the command line history.
     */
    void showPrevCommandLine();

    /**
     * Set the command line to the next one from the command line history.
     */
    void showNextCommandLine();

    /**
     * Provide assistance according to the command line.
     */
    void autoComplete(String commandLine);

    /**
     * Execute the command line.
     */
    void execute(String commandLine);
}

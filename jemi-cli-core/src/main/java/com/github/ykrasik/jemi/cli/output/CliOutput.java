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

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface CliOutput {
    /**
     * Called before anything is printed, to allow the implementation to prepare itself.
     * Will not be called again before {@link #end()} is called.
     */
    void begin();

    /**
     * Called when all printing has finished.
     * {@link #begin()} will be called again before anything else is printed.
     */
    void end();

    CliConfig getConfig();

    void println(String text);

    void errorPrintln(String text);

    void setCommandLine(String commandLine);

    // TODO: Should change working directory should be through here ?
}

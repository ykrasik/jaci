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

package com.github.ykrasik.jaci.cli.output;

/**
 * Output source of the CLI. Can be thought of as the "screen", everything that is visible to a user,
 * except the command line.
 * Outputs are allowed to prepare for execution and flush after execution via {@link #begin()} and {@link #end()}.
 *
 * @author Yevgeny Krasik
 */
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

    /**
     * Print a single line to the output.
     * A new line is expected to be added after the text by the output implementation.
     *
     * @param text Text to print.
     */
    void println(String text);

    /**
     * Print a single error line to the output.
     * A new line is expected to be added after the text by the output implementation.
     *
     * @param text Text to print as an error.
     */
    void errorPrintln(String text);

    /**
     * Set the 'working directory' to the given path.
     * This is a visual detail that simply displays what the current 'working directory' is.
     *
     * @param workingDirectory Working directory to set.
     */
    void setWorkingDirectory(String workingDirectory);
}

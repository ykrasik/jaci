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

package com.github.ykrasik.jerminal.api.output.terminal;

/**
 * A simple display, in which everything is printed into text.
 *
 * @author Yevgeny Krasik
 */
public interface Terminal {
    /**
     * Clear the command line.
     */
    void clearCommandLine();

    /**
     * Set the command line to the given command line.
     */
    void setCommandLine(String commandLine);

    /**
     * Print the message.
     */
    void print(String message);

    /**
     * Print the message as an error.
     */
    void printError(String message);
}

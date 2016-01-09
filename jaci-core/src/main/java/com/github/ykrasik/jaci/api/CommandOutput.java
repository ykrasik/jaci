/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.api;

/**
 * Output source of a Command.
 * Can display info and error messages.
 * Each class with commands should have a private, non-final (uninitialized) field of this type
 * into which an implementation will be injected by the library when the class is processed.
 *
 * @author Yevgeny Krasik
 */
public interface CommandOutput {
    /**
     * Display a message.
     *
     * @param text Message to display.
     */
    void message(String text);

    /**
     * Display an error message.
     *
     * @param text Error message to display.
     */
    void error(String text);
}

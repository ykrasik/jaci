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
 * Output source of a Command.<br>
 * Methods annotated with {@link Command}(referred to as 'commands') <b>MUST</b> receive a {@link CommandOutput} as the first parameter.
 * Commands that do not have a {@link CommandOutput} as their first parameter will not be accepted.
 * This essentially means that commands must always be at least 1-arity.<br>
 * Methods annotated with {@link ToggleCommand}(referred to as 'toggle commands') aren't bound by this, and in fact
 * can't have access to a {@link CommandOutput}, because toggle commands aren't meant to be general purpose commands.
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
     * Display a message. Message will be formatted with {@link String#format(String, Object...)}.
     *
     * @param format String format.
     * @param args Args for the format.
     */
    void message(String format, Object... args);

    /**
     * Display an error message.
     *
     * @param text Error message to display.
     */
    void error(String text);

    /**
     * Display an error message. Message will be formatted with {@link String#format(String, Object...)}.
     *
     * @param format String format.
     * @param args Args for the format.
     */
    void error(String format, Object... args);
}

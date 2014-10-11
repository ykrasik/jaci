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

package com.github.ykrasik.jerminal.api.exception;

/**
 * An exception that can be thrown during execution of a command to signal an invalid state.
 *
 * @author Yevgeny Krasik
 */
public class ExecuteException extends Exception {
    public ExecuteException(String message) {
        super(message);
    }

    /**
     * Construct the exception with a message that will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format Format to use for the message.
     * @param args Args to use for the message.
     */
    public ExecuteException(String format, Object... args) {
        this(String.format(format, args));
    }
}
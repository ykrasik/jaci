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

package com.github.ykrasik.jaci.command;

import java.util.List;

/**
 * Container for args that were passed to the command.<br>
 * Args are accessed by position is via a stack-like API.
 * If the actual class of the popped parameter doesn't match it's expected class, an {@link IllegalArgumentException} will be thrown.
 * The order of the arg values is the same order that the parameters were declared in.
 *
 * @author Yevgeny Krasik
 */
public interface CommandArgs {
    /**
     * @return Parsed arguments as a {@link List}.
     */
    List<Object> getArgs();

    /**
     * @param <T> Type that the next argument to pop is expected to have.
     * @return The next argument of type {@code T}.
     * @throws IllegalArgumentException If there are no more arguments.
     * @throws ClassCastException If the next argument isn't of type {@code T}.
     */
    <T> T popArg();
}

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

package com.github.ykrasik.jaci.cli;

/**
 * CLI constants.
 *
 * @author Yevgeny Krasik
 */
// TODO: This is meh...
public final class CliConstants {
    private CliConstants() { }

    /**
     * String representing 'this' in a path.
     * For example: 'path/./to' is equal to 'path/to'.
     */
    public static final String PATH_THIS = ".";

    /**
     * String representing the parent directory in a path.
     * For example: 'path/to/..' is equal to 'path/to'.
     */
    public static final String PATH_PARENT = "..";

    /**
     * When passing values to command parameters from the CLI,
     * any value prefixed by this will indicate that the string afterwards is the name of a parameter.
     * This is called call-by-value.
     * For example:
     *   Suppose we have a command, 'testCommand', which takes 2 parameters called 'param1' &amp; 'param2' (in that order).
     * All of these are equal:
     * <ul>
     *     <li>testCommand x y</li>
     *     <li>testCommand -param1 x y</li>
     *     <li>testCommand x -param2 y</li>
     *     <li>testCommand -param2 y x</li>
     *     <li>testCommand -param1 x -param2 y</li>
     *     <li>testCommand -param2 y -param1 x</li>
     * </ul>
     */
    public static final String NAMED_PARAM_PREFIX = "-";

    public static final String NULL = "null";
}

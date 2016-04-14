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
 * Output source of the CLI to which text can be printed.
 * A CLI always has 2 of these - one for stdOut and one for stdErr.
 * I really wanted this to just be a java {@code Writer}, but GWT isn't compatible with anything under java.io :(
 *
 * @author Yevgeny Krasik
 */
public interface CliOutput {
    /**
     * Print a single line to the output.
     * A new line is expected to be added after the text by the output implementation.
     *
     * @param text Text to print.
     */
    void println(String text);
}

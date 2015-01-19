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

package com.github.ykrasik.jerminal.api.display.terminal;

/**
 * A simple display, in which everything is printed into text.
 *
 * @author Yevgeny Krasik
 */
public interface Terminal {
    /**
     * @return String that should be used as a tab character.
     */
    String getTab();

    /**
     * Called before any text is printed, to allow the {@link Terminal} to prepare itself.<br>
     * Will not be called again before {@link #end()} is called.
     */
    void begin();

    /**
     * Called when all text has been printed.<br>
     * {@link #begin()} will be called before any more text is printed.
     */
    void end();

    /**
     * Print the text with the specified color and move to the next line.
     *
     * @param text Text to print.
     * @param color Color to print the text with.
     */
    void println(String text, TerminalColor color);
}

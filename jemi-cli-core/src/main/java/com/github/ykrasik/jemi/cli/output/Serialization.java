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

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A buffer of lines to be printed.
 *
 * @author Yevgeny Krasik
 */
public class Serialization implements Iterable<String> {
    /**
     * Indentation unit.
     */
    private final String tab;

    /**
     * The lines.
     */
    private final List<String> lines = new ArrayList<>();

    /**
     * Current line.
     */
    private StringBuilder sb = new StringBuilder();

    /**
     * Indent level.
     */
    private int indent;

    /**
     * Whether indent was appended to the current line. Reset every time a new line is opened.
     */
    private boolean needIndent = true;

    /**
     * @param tab String to use as tab (1 unit of indentation).
     */
    public Serialization(@NonNull String tab) {
        this.tab = tab;
    }

    /**
     * Increase the indent level.
     */
    public void incIndent() {
        indent++;
    }

    /**
     * Decrease the indent level.
     */
    public void decIndent() {
        indent--;
        if (indent < 0) {
            throw new IllegalArgumentException("Invalid indent: " + indent);
        }
    }

    /**
     * Append the string to the current line.
     *
     * @param str String to append.
     * @return {@code this}, for chaining.
     */
    public Serialization append(String str) {
        indentIfNecessary();
        sb.append(str);
        return this;
    }

    /**
     * Append the character to the current line.
     *
     * @param ch Character to append.
     * @return {@code this}, for chaining.
     */
    public Serialization append(char ch) {
        indentIfNecessary();
        sb.append(ch);
        return this;
    }

    /**
     * Open a new line.
     * Adds the current line to the buffer.
     *
     * @return {@code this}, for chaining.
     */
    public Serialization newLine() {
        lines.add(sb.toString());
        sb = new StringBuilder();
        needIndent = true;
        return this;
    }

    private void indentIfNecessary() {
        if (needIndent) {
            appendIndent();
            needIndent = false;
        }
    }

    private void appendIndent() {
        for (int i = 0; i < indent; i++) {
            sb.append(tab);
        }
    }

    @Override
    public Iterator<String> iterator() {
        return lines.iterator();
    }

    @Override
    public String toString() {
        return lines.toString();
    }
}

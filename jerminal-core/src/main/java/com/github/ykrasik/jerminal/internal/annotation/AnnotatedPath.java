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

package com.github.ykrasik.jerminal.internal.annotation;

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.api.annotation.ShellPath;

import java.util.Objects;

/**
 * Represents the path specified by an annotation.<br>
 * Paths can be either global or local, and support path composition.
 *
 * @author Yevgeny Krasik
 */
public class AnnotatedPath {
    private static final AnnotatedPath ROOT = new AnnotatedPath(ShellConstants.FILE_SYSTEM_DELIMITER, false);
    private static final AnnotatedPath EMPTY = new AnnotatedPath("", false);

    private final String path;
    private final boolean global;

    private AnnotatedPath(String path, boolean global) {
        this.path = path;
        this.global = global;
    }

    /**
     * @return The path. If {@link #isGlobal()} is true, this may return anything.
     *         Otherwise, must return a valid path.
     */
    public String getPath() {
        return path;
    }

    /**
     * @return True if this path is global.
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Compose a new path out of this one and the received path.<br>
     * The other path usually has priority over this path:<br>
     * If this is a global path, the composed path will be the other path (whether it's global or not).
     * If this is not a global path but the other path is, the composed path will again be the other path.
     * Otherwise, the composed path will be this path + other path.
     *
     * @param other Path to compose against.
     * @return A composed {@link AnnotatedPath} according to the above rules.
     */
    public AnnotatedPath compose(AnnotatedPath other) {
        if (global || other.global) {
            return other;
        }

        // Make sure the first path ends with a delimiter and the second path doesn't start with one.
        final String path1 = endsWithDelimiter(this.path) ? this.path : this.path + ShellConstants.FILE_SYSTEM_DELIMITER;
        final String path2 = startsWithDelimiter(other.path) ? other.path.substring(1) : other.path;
        return new AnnotatedPath(path1 + path2, false);
    }

    /**
     * @param annotation Input annotation.
     * @return An {@link AnnotatedPath} constructed from the annotation.
     */
    public static AnnotatedPath fromAnnotation(ShellPath annotation) {
        // Make sure the path always ends with a delimiter - easier to work with.
        final String path = Objects.requireNonNull(annotation.value());
        final String pathToUse = path.endsWith(ShellConstants.FILE_SYSTEM_DELIMITER) ? path : path + ShellConstants.FILE_SYSTEM_DELIMITER;
        return new AnnotatedPath(pathToUse, annotation.global());
    }

    /**
     * @return An {@link AnnotatedPath} that points to root.
     */
    public static AnnotatedPath root() {
        return ROOT;
    }

    /**
     * @return An {@link AnnotatedPath} that doesn't point to anything.
     */
    public static AnnotatedPath empty() {
        return EMPTY;
    }

    private static boolean endsWithDelimiter(String path) {
        return path.endsWith(ShellConstants.FILE_SYSTEM_DELIMITER);
    }

    private static boolean startsWithDelimiter(String path) {
        return path.startsWith(ShellConstants.FILE_SYSTEM_DELIMITER);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnnotatedPath{");
        sb.append("path='").append(path).append('\'');
        sb.append(", global=").append(global);
        sb.append('}');
        return sb.toString();
    }
}

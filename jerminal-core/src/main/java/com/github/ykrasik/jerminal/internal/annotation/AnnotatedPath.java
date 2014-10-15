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
 * Represents the path specified by an annotation.
 *
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class AnnotatedPath {
    private static final AnnotatedPath ROOT = new AnnotatedPath(ShellConstants.FILE_SYSTEM_DELIMITER, false);
    private static final AnnotatedPath EMPTY = new AnnotatedPath("", false);

    private final String path;
    private final boolean global;

    private AnnotatedPath(String path, boolean global) {
        this.path = path;
        this.global = global;
    }

    public String getPath() {
        return path;
    }

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
     * @param other
     * @return
     */
    // FIXME: JavaDoc
    public AnnotatedPath compose(AnnotatedPath other) {
        if (global || other.global) {
            return other;
        }

        final String composedPath;
        if (path.endsWith(ShellConstants.FILE_SYSTEM_DELIMITER) &&
            other.path.startsWith(ShellConstants.FILE_SYSTEM_DELIMITER)) {
            // Can't concat the 2 paths, will create an illegal path.
            // Remove one of the delimiters.
            composedPath = path + other.path.substring(1);
        } else {
            composedPath = path + other.path;
        }
        return new AnnotatedPath(composedPath, false);
    }

    public static AnnotatedPath fromAnnotation(ShellPath annotation) {
        return new AnnotatedPath(Objects.requireNonNull(annotation.value()), annotation.global());
    }

    public static AnnotatedPath root() {
        return ROOT;
    }

    public static AnnotatedPath empty() {
        return EMPTY;
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

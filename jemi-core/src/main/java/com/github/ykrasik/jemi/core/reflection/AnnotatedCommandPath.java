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

package com.github.ykrasik.jemi.core.reflection;

import com.github.ykrasik.jemi.api.Constants;
import lombok.NonNull;

/**
 * Represents the path specified by an annotation.<br>
 * Paths support composition and overriding, both expressed through {@link #compose(AnnotatedCommandPath)}.
 *
 * @author Yevgeny Krasik
 */
public class AnnotatedCommandPath {
    private static final AnnotatedCommandPath ROOT = new AnnotatedCommandPath(String.valueOf(Constants.PATH_DELIMITER), false);
    private static final AnnotatedCommandPath EMPTY = new AnnotatedCommandPath("", false);

    private final String path;
    private final boolean override;

    /**
     * @param path Path to use.
     * @param override Whether the path should override or be appended to other paths.
     */
    public AnnotatedCommandPath(@NonNull String path, boolean override) {
        // Make sure the path always ends with a delimiter - easier to work with.
        this.path = appendDelimiterIfNecessary(path);
        this.override = override;
    }

    /**
     * @return An {@link AnnotatedCommandPath} that points to root.
     */
    public static AnnotatedCommandPath root() {
        return ROOT;
    }

    /**
     * @return An {@link AnnotatedCommandPath} that doesn't point to anything.
     */
    public static AnnotatedCommandPath empty() {
        return EMPTY;
    }

    /**
     * @return Path from root, in {@link String} form.
     */
    public String getPath() {
        return path;
    }

    /**
     * Compose a new path out of this one and the received path:
     * <ul>
     *     <li>If this path is set to override via {@link #override}, this path will be returned.</li>
     *     <li>If the other path is set to override via {@link #override}, the other path will be returned.</li>
     *     <li>Otherwise, this other path will be appended to this path.</li>
     * </ul>
     *
     * @param other Path to compose against.
     * @return A composed {@link AnnotatedCommandPath} according to the above rules.
     */
    public AnnotatedCommandPath compose(AnnotatedCommandPath other) {
        if (override) {
            return this;
        }
        if (other.override) {
            return other;
        }

        // Make sure the first path ends with a delimiter and the second path doesn't start with one.
        final String path1 = appendDelimiterIfNecessary(this.path);
        final String path2 = removeLeadingDelimiter(other.path);
        return new AnnotatedCommandPath(path1 + path2, false);
    }

    private String appendDelimiterIfNecessary(String path) {
        return path.endsWith(Constants.PATH_DELIMITER_STRING) ? path : path + Constants.PATH_DELIMITER_STRING;
    }

    private String removeLeadingDelimiter(String path) {
        return path.startsWith(Constants.PATH_DELIMITER_STRING) ? path.substring(1) : path;
    }
}

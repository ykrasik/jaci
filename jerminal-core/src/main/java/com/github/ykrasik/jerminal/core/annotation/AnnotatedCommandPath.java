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

package com.github.ykrasik.jerminal.core.annotation;

import com.github.ykrasik.jerminal.api.Constants;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Represents the path specified by an annotation.<br>
 * Paths can be either global or local, and support path composition.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@Data
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotatedCommandPath {
    /**
     * An {@link AnnotatedCommandPath} that points to root.
     */
    @Getter private static final AnnotatedCommandPath root = new AnnotatedCommandPath(String.valueOf(Constants.PATH_DELIMITER), false);

    /**
     * An {@link AnnotatedCommandPath} that doesn't point to anything.
     */
    @Getter private static final AnnotatedCommandPath empty = new AnnotatedCommandPath("", false);

    /**
     * Path from root.
     */
    private final String path;

    /**
     * Whether this path should override or be appended to other paths.
     */
    // FIXME: Is override a good idea?
    @Getter(AccessLevel.NONE) private final boolean override;

    /**
     * Compose a new path out of this one and the received path.<br>
     * The other path usually has priority over this path:<br>
     * If this is a global path, the composed path will be the other path (whether it's global or not).
     * If this is not a global path but the other path is, the composed path will again be the other path.
     * Otherwise, the composed path will be this path + other path.
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

    /**
     * Create a path from the given parameters.
     *
     * @param path Path to use.
     * @param override Whether the path should override or be appended to other paths.
     * @return An {@link AnnotatedCommandPath} constructed from the parameters.
     */
    public static AnnotatedCommandPath from(@NonNull String path, boolean override) {
        // Make sure the path always ends with a delimiter - easier to work with.
        final String pathToUse = appendDelimiterIfNecessary(path);
        return new AnnotatedCommandPath(pathToUse, override);
    }

    private static String appendDelimiterIfNecessary(String path) {
        return endsWithDelimiter(path) ? path : path + Constants.PATH_DELIMITER_STRING;
    }

    private static String removeLeadingDelimiter(String path) {
        return startsWithDelimiter(path) ? path.substring(1) : path;
    }

    private static boolean endsWithDelimiter(String path) {
        return path.endsWith(Constants.PATH_DELIMITER_STRING);
    }

    private static boolean startsWithDelimiter(String path) {
        return path.startsWith(Constants.PATH_DELIMITER_STRING);
    }
}

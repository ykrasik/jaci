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

package com.github.ykrasik.jaci.path;

import com.github.ykrasik.jaci.util.string.StringUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the path in the system.<br>
 * Paths are delimited by '/'. Path appending is supported via {@link #append(ParsedPath)}.
 *
 * @author Yevgeny Krasik
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParsedPath implements Iterable<String> {
    private static final ParsedPath ROOT = new ParsedPath(true, Collections.<String>emptyList());
    private static final ParsedPath EMPTY = new ParsedPath(false, Collections.<String>emptyList());

    private final boolean startsWithDelimiter;
    private final List<String> elements;

    /**
     * @return A copy of this path without the last element.
     */
    public ParsedPath withoutLastElement() {
        if (this == EMPTY) {
            throw new IllegalArgumentException("Empty path!");
        }
        if (this == ROOT) {
            // TODO: Make sure this doesn't mask any bugs.
            return ROOT;
        }

        // FIXME: Something about this isn't quite right.. unit tests this!!!
        final List<String> newElements = elements.subList(0, elements.size() - 1);
        return new ParsedPath(startsWithDelimiter, newElements);
    }

    /**
     * @return Last path element.
     */
    public String getLastElement() {
        // TODO: Make sure this doesn't mask potential bugs.
        if (elements.isEmpty()) {
            return "";
        }
        return elements.get(elements.size() - 1);
    }

    /**
     * @return Whether the path starts from the delimiter '/'.
     */
    public boolean startsWithDelimiter() {
        return startsWithDelimiter;
    }

    /**
     * @return Whether the path contains the delimiter '/';
     */
    public boolean containsDelimiter() {
        return startsWithDelimiter || elements.size() > 1;
    }

    /**
     * Append the received path to this path.
     *
     * @param other Path to append to this path.
     * @return A {@link ParsedPath} resulting from appending the received path to this path.
     */
    public ParsedPath append(ParsedPath other) {
        final List<String> composedPath = new ArrayList<>(this.elements.size() + other.elements.size());
        composedPath.addAll(this.elements);
        composedPath.addAll(other.elements);
        return new ParsedPath(startsWithDelimiter, composedPath);
    }

    @Override
    public Iterator<String> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return (startsWithDelimiter ? "/" : "") + StringUtils.join(elements, "/");
    }

    /**
     * Returns a {@link ParsedPath} representing the root path.
     * The root path is unique in that it is the only path that is allowed to start from the delimiter '/' and have no elements.
     *
     * @return A path representing the root path.
     */
    public static ParsedPath root() {
        return ROOT;
    }

    /**
     * Create a path that is expected to represent a path to a directory.
     * This means that if the path ends with a delimiter '/', it is considered the same as if it didn't.
     * i.e. path/to and path/to/ are considered the same - a path with 2 elements: 'path' and 'to'.
     *
     * @param rawPath Path to parse.
     * @return A {@link ParsedPath} out of the given path.
     * @throws IllegalArgumentException If any element along the path is empty.
     */
    public static ParsedPath toDirectory(@NonNull String rawPath) {
        final String path = rawPath.trim();
        if (path.isEmpty()) {
            // TODO: Is This legal?
            return EMPTY;
        }
        if ("/".equals(path)) {
            // TODO: Is this special case needed?
            return ROOT;
        }

        final boolean startsWithDelimiter = path.startsWith("/");

        // Remove the trailing delimiter.
        // This allows us to treat paths that end with a delimiter as paths to the last directory on the path.
        // i.e. path/to and path/to/ are the same - a path with 2 elements: 'path' and 'to'.
        final List<String> pathElements = splitPath(path, false);
        return new ParsedPath(startsWithDelimiter, pathElements);
    }

    /**
     * Create a path that is expected to represent a path to any entry.
     * This is different from {@link #toDirectory(String)} in that if the path ends with a delimiter '/',
     * it is <b>not</b> considered the same as if it didn't.
     * i.e. path/to would be a path with 2 elements: 'path' and 'to', but
     * but  path/to/ would be a path with 3 elements: 'path', 'to' and an empty element ''.
     *
     * @param rawPath Path to parse.
     * @return A {@link ParsedPath} out of the given path.
     * @throws IllegalArgumentException If any element along the path except the last one is empty.
     */
    public static ParsedPath toEntry(@NonNull String rawPath) {
        final String path = rawPath.trim();

        final boolean startsWithDelimiter = path.startsWith("/");

        // Keep the trailing delimiter.
        // This allows us to treat paths that end with a delimiter differently from paths that don't.
        // i.e. path/to would be a path with 2 elements: 'path' and 'to', but
        // but  path/to/ would be a path with 3 elements: 'path', 'to' and an empty element ''.
        final List<String> pathElements = splitPath(path, true);
        return new ParsedPath(startsWithDelimiter, pathElements);
    }

    private static List<String> splitPath(String rawPath, boolean allowTrailingDelimiter) {
        final String[] rawElements = doSplitPath(rawPath, allowTrailingDelimiter);
        final List<String> pathElements = new ArrayList<>(rawElements.length);
        for (int i = 0; i < rawElements.length; i++) {
            final String element = rawElements[i];
            final String trimmedElement = element.trim();
            if (i < rawElements.length - 1) {
                // Only the last element can be allowed to be empty.
                if (trimmedElement.isEmpty()) {
                    throw new IllegalArgumentException(String.format("Invalid path: '%s'", rawPath));
                }
            }
            pathElements.add(trimmedElement);
        }
        return pathElements;
    }

    private static String[] doSplitPath(String rawPath, boolean allowTrailingDelimiter) {
        final String pathWithoutDelimiters;
        final int limit;
        if (allowTrailingDelimiter) {
            pathWithoutDelimiters = StringUtils.removeLeadingDelimiter(rawPath, "/");
            limit = -1;
        } else {
            pathWithoutDelimiters = StringUtils.removeLeadingAndTrailingDelimiter(rawPath, "/");
            limit = 0;
        }
        return pathWithoutDelimiters.split("/", limit);
    }
}

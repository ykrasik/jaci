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

package com.github.ykrasik.jemi.path;

import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.string.StringUtils;
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
 * Paths are delimited by '/', and support composition via {@link #compose(ParsedPath)}.
 *
 * @author Yevgeny Krasik
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParsedPath implements Iterable<String> {
    private static final ParsedPath ROOT = new ParsedPath("/", Collections.<String>emptyList());
    private static final ParsedPath EMPTY = new ParsedPath("", Collections.<String>emptyList());

    private final String rawPath;
    private final List<String> elements;

    // TODO: JavaDoc
    public ParsedPath withoutLastElement() {
        if (this == EMPTY) {
            throw new IllegalArgumentException("Empty path!");
        }
        if (this == ROOT) {
            return ROOT;
        }

        // FIXME: Something about this isn't quite right.. unit tests this!!!
        final String newRawPath = getRawPathWithoutLastElement();
        final List<String> newElements = elements.subList(0, elements.size() - 1);
        return new ParsedPath(newRawPath, newElements);
    }

    private String getRawPathWithoutLastElement() {
        final int index = rawPath.lastIndexOf('/');
        final String newRawPath = (index == -1) ? "" : rawPath.substring(0, index);

        // If the original path started from root, this property must be preserved.
        if (startsWithDelimiter()) {
            return newRawPath.startsWith("/") ? newRawPath : '/' + newRawPath;
        } else {
            return newRawPath;
        }
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
        return rawPath.startsWith("/");
    }

    /**
     * @return Whether the path ends with the delimiter '/'.
     */
    public boolean endsWithDelimiter() {
        return rawPath.endsWith("/");
    }

    /**
     * @return Whether the path contains the delimiter '/';
     */
    public boolean containsDelimiter() {
        return elements.size() > 1 || startsWithDelimiter() || endsWithDelimiter();
    }

    /**
     * Append the received path to this path.
     *
     * @param other Path to compose against.
     * @return A {@link ParsedPath} resulting from appending the received path to this path.
     */
    public ParsedPath compose(ParsedPath other) {
        final String composedRawPath = appendDelimiterIfNecessary(rawPath) + removeLeadingDelimiter(other.rawPath);
        final List<String> composedPath = new ArrayList<>(this.elements.size() + other.elements.size());
        composedPath.addAll(this.elements);
        composedPath.addAll(other.elements);
        return new ParsedPath(composedRawPath, composedPath);
    }

    private String appendDelimiterIfNecessary(String path) {
        return path.endsWith("/") ? path : path + '/';
    }

    private String removeLeadingDelimiter(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    @Override
    public Iterator<String> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return rawPath;
    }

    // TODO: JavaDoc
    public static ParsedPath from(@NonNull String path) {
        final String trimmedPath = path.trim();
        if (trimmedPath.isEmpty()) {
            return EMPTY;
        }
        if ("/".equals(trimmedPath)) {
            return ROOT;
        }

        final String pathWithoutDelimiters = StringUtils.removeLeadingAndTrailingDelimiter(trimmedPath, "/");
        final List<String> pathElements = splitPath(pathWithoutDelimiters);
        return new ParsedPath(trimmedPath, pathElements);
    }

    private static List<String> splitPath(String path) {
        final String[] rawElements = path.split("/");
        final List<String> pathElements = new ArrayList<>(rawElements.length);
        for (String element : rawElements) {
            final Opt<String> nonEmptyElement = StringUtils.getNonEmptyString(element);
            if (!nonEmptyElement.isPresent()) {
                throw new IllegalArgumentException(String.format("Invalid path: '%s'", path));
            }
            pathElements.add(nonEmptyElement.get());
        }
        return pathElements;
    }

    // TODO: JavaDoc
    public static ParsedPath root() {
        return ROOT;
    }
}

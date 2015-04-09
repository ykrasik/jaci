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

package com.github.ykrasik.jerminal.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An identifier for an entity - it's name and description.
 *
 * @author Yevgeny Krasik
 */
public class Identifier {
    /**
     * A pattern that matches any non-word character: ^[a-zA-Z_0-9]
     */
    private static final Pattern ILLEGAL_NAME_PATTERN = Pattern.compile("\\W");

    /**
     * The name of this entity.
     */
    @Getter
    private final String name;

    /**
     * The description of this entity.
     */
    @Getter
    private final String description;

    public Identifier(@NonNull String name, @NonNull String description) {
        this.name = name;
        this.description = description;
        assertValidName();
    }

    private void assertValidName() {
        final Matcher matcher = ILLEGAL_NAME_PATTERN.matcher(name);
        if (matcher.matches()) {
            final int index = matcher.regionStart();
            throw new IllegalArgumentException(String.format("Invalid character in name: name='%s', index=%d", name, index));
        }
    }

    @Override
    public String toString() {
        return name + " : " + description;
    }

    /**
     * A {@link Comparator} that compares {@link Identifier}s according to {@link Identifier#getName()}.
     */
    @Accessors(fluent = true)
    @Getter
    private static Comparator<Identifier> nameComparator = new Comparator<Identifier>() {
        @Override
        public int compare(Identifier o1, Identifier o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}

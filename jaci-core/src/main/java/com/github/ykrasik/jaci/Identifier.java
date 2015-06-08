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

package com.github.ykrasik.jaci;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An identifier for an entity - it's name and description.
 *
 * @author Yevgeny Krasik
 */
@EqualsAndHashCode
public final class Identifier {
    /**
     * A pattern that matches any strings that start with a letter and are alphanumeric.
     */
    private static final Pattern LEGAL_NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w]*");

    private final String name;
    private final String description;

    public Identifier(@NonNull String name, @NonNull String description) {
        this.name = name;
        this.description = description;
        assertValidName();
    }

    private void assertValidName() {
        final Matcher matcher = LEGAL_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            final int index = matcher.regionStart();
            throw new IllegalArgumentException(String.format("Names must be alphanumeric and start with a letter: name='%s', index=%d", name, index));
        }
    }

    /**
     * @return The name of this entity.
     */
    public String getName() {
        return name;
    }

    /**
     * @return  The description of this entity.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " : " + description;
    }


    private static Comparator<Identifier> NAME_COMPARATOR = new Comparator<Identifier>() {
        @Override
        public int compare(Identifier o1, Identifier o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    /**
     * @return A {@link Comparator} that compares {@link Identifier}s according to {@link Identifier#getName()}.
     */
    public static Comparator<Identifier> nameComparator() {
        return NAME_COMPARATOR;
    }
}

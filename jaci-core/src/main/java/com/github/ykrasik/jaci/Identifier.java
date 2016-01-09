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

import java.util.Comparator;
import java.util.Objects;

/**
 * An identifier for an entity - it's name and description.
 *
 * @author Yevgeny Krasik
 */
public final class Identifier {
    private final String name;
    private final String description;

    public Identifier(String name, String description) {
        this.name = assertValidName(name);
        this.description = Objects.requireNonNull(description, "description");
    }

    private String assertValidName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name: Names cannot be empty!");
        }
        if (!Character.isLetter(name.charAt(0))) {
            throw new IllegalArgumentException("Invalid name: Names must start with a letter: " + name);
        }
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isLetterOrDigit(name.charAt(i))) {
                throw new IllegalArgumentException("Invalid name: Names must be alpha-number: " + name);
            }
        }
        return name;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Identifier that = (Identifier) o;

        if (!name.equals(that.name)) {
            return false;
        }
        return description.equals(that.description);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
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

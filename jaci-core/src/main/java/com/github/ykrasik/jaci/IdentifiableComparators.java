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

/**
 * {@link Comparator}s for {@link Identifiable} entities.
 *
 * @author Yevgeny Krasik
 */
public final class IdentifiableComparators {
    private IdentifiableComparators() { }

    private static Comparator<Identifiable> NAME_COMPARATOR = new Comparator<Identifiable>() {
        @Override
        public int compare(Identifiable o1, Identifiable o2) {
            return Identifier.nameComparator().compare(o1.getIdentifier(), o2.getIdentifier());
        }
    };

    /**
     * @return A {@link Comparator} that compares 2 {@link Identifiable}s according to {@link Identifier#getName()}.
     */
    public static Comparator<Identifiable> nameComparator() {
        return NAME_COMPARATOR;
    }
}

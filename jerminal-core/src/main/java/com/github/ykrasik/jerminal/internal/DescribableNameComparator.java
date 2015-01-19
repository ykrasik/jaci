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

package com.github.ykrasik.jerminal.internal;

import java.util.Comparator;

/**
 * A {@link Comparator} that compares {@link Describable} according to {@link Describable#getName()}.
 *
 * @author Yevgeny Krasik
 */
public class DescribableNameComparator implements Comparator<Describable> {
    @Override
    public int compare(Describable o1, Describable o2) {
        return o1.getName().compareTo(o2.getName());
    }
}

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

package com.github.ykrasik.jemi.core.reflection.command;

import com.github.ykrasik.jemi.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jemi.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A {@link Supplier} that returns the inverse of the current {@link ToggleCommandStateAccessor#get()}.
 */
@RequiredArgsConstructor
public class ToggleCommandAccessorDefaultValueSupplier implements Supplier<Boolean> {
    @NonNull private final ToggleCommandStateAccessor accessor;

    @Override
    public Boolean get() {
        return !accessor.get();
    }
}

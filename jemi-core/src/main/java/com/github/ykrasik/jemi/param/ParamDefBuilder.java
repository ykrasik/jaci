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

package com.github.ykrasik.jemi.param;

import com.github.ykrasik.jemi.util.function.Supplier;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface ParamDefBuilder<T extends ParamDef<E>, E> {
    ParamDefBuilder<T, E> setDescription(String description);

    ParamDefBuilder<T, E> setOptional(E defaultValue);
    ParamDefBuilder<T, E> setOptional(Supplier<E> defaultValueSupplier);
}

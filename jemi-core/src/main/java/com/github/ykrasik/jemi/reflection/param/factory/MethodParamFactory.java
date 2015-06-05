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

package com.github.ykrasik.jemi.reflection.param.factory;

import com.github.ykrasik.jemi.param.ParamDef;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.reflection.ReflectionParameter;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface MethodParamFactory<T extends ParamDef<?>> {
    Opt<T> create(Object instance, ReflectionParameter param) throws Exception;
}

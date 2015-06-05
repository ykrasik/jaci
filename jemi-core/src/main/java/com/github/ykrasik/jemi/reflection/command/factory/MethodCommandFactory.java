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

package com.github.ykrasik.jemi.reflection.command.factory;

import com.github.ykrasik.jemi.command.CommandDef;
import com.github.ykrasik.jemi.util.opt.Opt;

import java.lang.reflect.Method;

/**
 * Creates {@link CommandDef}s out of {@link Method}s. Is not required to support all methods, may signal that a method
 * is not supported by returning an {@code absent} value from {@link #create(Object, Method)}.
 *
 * @author Yevgeny Krasik
 */
public interface MethodCommandFactory {
    /**
     * Process the method and create a {@link CommandDef} out of it, if this factory can accept this method.
     *
     * @param instance Instance of a class to which this method belongs.
     * @param method Method to be processed.
     * @return A {@code present} {@link CommandDef} if the method is accepted by this factory.
     * @throws Exception If any error occurs.
     */
    Opt<CommandDef> create(Object instance, Method method) throws Exception;
}

/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated parameter is a double parameter.<br>
 * Optional annotation - any parameter not annotated will be considered a mandatory parameter
 * and will have a name and description generated for it.<br>
 * <br>
 * Parameters may be optional - indicating that they do not need an explicit value to be passed and will use a default
 * value if not assigned to a value. This can be set via {@link #optional()}.<br>
 * There are 2 ways of obtaining a default value for an optional parameter:
 * <ol>
 *     <li>Constant value - Return a constant default value. Can be set via {@link #defaultValue()}.</li>
 *     <li>Dynamic value - Return a default value that is computed at runtime, by invoking a method that takes no args
 *                         and returns a {@link Double} or {@code double} (called the 'supplier').
 *                         The supplier must be a method in the same class, and may be private.
 *                         Can be set via {@link #defaultValueSupplier()}.</li>
 * </ol>
 * If {@link #defaultValueSupplier()} returns a non-empty {@code String}, it overrides the value returned by {@link #defaultValue()}
 * and the parameter will have its default value computed dynamically at runtime.
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DoubleParam {
    /**
     * @return Parameter name. If empty, a default name will be generated.
     */
    String value() default "";

    /**
     * @return Parameter description. If empty, a default description will be generated.
     */
    String description() default "";

    /**
     * If this returns {@code true}, the annotated parameter will be considered optional.<br>
     * The default value will be either the value returned by {@link #defaultValue()} or {@link #defaultValueSupplier()}.
     * If {@link #defaultValueSupplier()} returns a non-empty {@code String}, it overrides the value returned by {@link #defaultValue()}
     * and the parameter will have its default value computed dynamically at runtime.
     *
     * @return True if this parameter is optional.
     */
    boolean optional() default false;

    /**
     * Constant default value - If this parameter isn't explicitly bound, this will be the default value.
     * Only taken into consideration if {@link #optional()} returns {@code true} and {@link #defaultValueSupplier()}
     * returns an empty {@code String}.
     *
     * @return The default value the parameter should use if a value isn't explicitly passed.
     */
    double defaultValue() default 0.0;

    /**
     * Dynamic default value - If this parameter isn't explicitly bound, the parameter's default value will be the
     * value returned by invoking a method with the given name (called the 'supplier').
     * The supplier must be in the same class, take no args and return a {@link Double} or {@code double}. May be private.<br>
     * Only taken into consideration if {@link #optional()} returns {@code true}.
     *
     * @return The default value supplier method the parameter should invoke if a value isn't explicitly passed.
     */
    String defaultValueSupplier() default "";
}

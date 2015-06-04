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

package com.github.ykrasik.jemi.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated parameter is a string parameter.<br>
 * Optional annotation - any parameter not annotated will be considered a mandatory parameter that accepts
 * all strings, and will have a name and description generated for it.
 * <br>
 * Parameters may be optional - indicating that they do not need an explicit value to be passed and will use a default
 * value if not assigned to a value. This can be set via {@link #optional()}.<br>
 * There are 2 ways of obtaining a default value for an optional parameter:
 * <ol>
 *     <li>Constant value - Return a constant default value. Can be set via {@link #defaultValue()}.</li>
 *     <li>Dynamic value - Return a default value that is computed at runtime, by invoking a method that takes no args
 *                         and returns a {@link String} (called the 'supplier').
 *                         The supplier must be a method in the same class, and may be private.
 *                         Can be set via {@link #defaultValueSupplier()}.</li>
 * </ol>
 * If {@link #defaultValueSupplier()} returns a non-empty {@code String}, it overrides the value returned by {@link #defaultValue()}
 * and the parameter will have its default value computed dynamically at runtime.<br>
 * <br>
 * String parameters can be constrained to only accept certain values. There are 3 types of constraints:
 * <ol>
 *     <li>None - All values are accepted.</li>
 *     <li>Static - Only pre-defined values are accepted. Can be set through {@link #accepts()}.</li>
 *     <li>Dynamic - The acceptable values are calculated at runtime, by invoking a method that takes no args and returns
 *                   an array of {@link String} (called the 'supplier').
 *                   The supplier must be a method in the same class and may be private.
 *                   Can be set through {@link #supplier()}.</li>
 * </ol>
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface StringParam {
    /**
     * @return Parameter name. If empty, a default name will be generated.
     */
    String value() default "";

    /**
     * @return Parameter description. If empty, a default description will be generated.
     */
    String description() default "";

    /**
     * Static constraint - Constrain the parameter to only accept a pre-defined set of Strings.
     * By default accepts all Strings.
     * Only taken into consideration if {@link #supplier()} returns an empty {@code String}.
     *
     * @return An array of constant String values that this parameter can accept. If this is an empty
     *         array, the parameter will accept any String value. If not, the parameter will only accept
     *         values that are contained in the array. Only has effect if {@link #supplier()} returns an empty {@code String}.
     */
    String[] accepts() default {};

    /**
     * Dynamic constraint - Constrain the parameter to only accept values returned by invoking the supplier method.
     * The supplier method must be in the same class as the command for which this is a parameter, take no args and
     * return an array of {@code String}s. If the supplier returns an empty array, all values will be permitted.<br>
     * <br>
     * If the value of this property is not an empty {@code String}, it will override {@link #accepts()}
     * and this parameter will be dynamically constrained.
     *
     * @return The name of a method that takes no args and returns an array of {@link String} defined in the same class
     *         (may be private). This method will be invoked at runtime to determine the possible values for this parameter.
     *         If the supplier returns an empty array, all values will be permitted.
     */
    String supplier() default "";

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
    String defaultValue() default "";

    /**
     * Dynamic default value - If this parameter isn't explicitly bound, the parameter's default value will be the
     * value returned by invoking a method with the given name (called the 'supplier').
     * The supplier must be in the same class, take no args and return a {@link String}. May be private.<br>
     * Only taken into consideration if {@link #optional()} returns {@code true}.
     *
     * @return The default value supplier method the parameter should invoke if a value isn't explicitly passed.
     */
    String defaultValueSupplier() default "";
}
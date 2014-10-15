/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the parameter annotated by this annotation is a String parameter.<br>
 * Optional, any parameter not annotated will be considered a mandatory String parameter that accepts
 * all Strings, and will have a name and description generated for it.
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
     * @return True if this parameter is optional.
     *         Optional parameters will use the value returned by {@link #defaultValue()} if they weren't passed.
     */
    boolean optional() default false;

    /**
     * @return The default value acceptable by this parameter if a value isn't explicitly passed.
     *         Only used if {@link #optional()} is true.
     */
    String defaultValue() default "";

    /**
     * @return An array of constant String values that this parameter should accept. If this is an empty
     *         array, the parameter will accept any String value. If not, the parameter will only accept
     *         values that are contained in the array.
     */
    String[] acceptsValues() default {};
}

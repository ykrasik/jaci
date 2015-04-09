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

package com.github.ykrasik.jerminal.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the path under which commands should be added. The path should be delimited with {@link Constants#PATH_DELIMITER}.
 * The initial delimiter is unnecessary, as paths always start from the root.<br>
 * <br>
 * If a class is annotated with this, all it's methods annotated with {@link Command}
 * will inherit the path of this class. This is called the 'top level path'.<br>
 * <br>
 * Each method annotated with {@link Command} can also specify it's own path, with the following conditions:<br>
 *   If the annotation is set to override (via {@link #override()},
 *     this annotation's {@link #value()} will replace the class's 'top level path'.<br>
 *   If the annotation is set not set to override (via {@link #override()},
 *     this annotation's {@link #value()} will be appended to the class's 'top level path'.<br>
 * <br>
 * Optional annotation, classes not annotated will be considered as though they are under root and methods not annotated
 * will inherit the class's annotation.
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
// FIXME: Separate into 2 annotations: CommandHierarchyPath & CommandPath, as override=true doesn't make sense on a class.
public @interface CommandPath {
    /**
     * @return The path. Must be a valid path and use '/' as delimiters.<br>
     *         Any directory along the path that doesn't exist will be created.
     */
    String value() default "";

    /**
     * @return Whether to append this path to the current 'top level path', or override it with this path.
     */
    boolean override() default false;
}

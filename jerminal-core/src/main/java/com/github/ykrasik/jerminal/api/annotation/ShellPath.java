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

package com.github.ykrasik.jerminal.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the path under which commands should be added.<br>
 * If a class is annotated with this, all it's methods that are annotated with {@link Command}, {@link ToggleCommand} or
 * {@link CommandFactory} will inherit the path of this class. This is referred to as the 'top level path'.<br>
 * Each method annotated with the above mentioned annotations can also specify it's own path, with the following conditions:<br>
 *   If the top level path is global, any method annotation overrides it.<br>
 *   If the top level path is not global but the method annotation is, the method annotation overrides it.<br>
 *   Else (neither annotations is global), the path of the command will be the top level path composed with
 *     the method annotation path.<br>
 * <br>
 * Optional annotation, classes not annotated will be considered as though they are under root and methods not annotated
 * will inherit the class's annotation.
 *
 * @author Yevgeny Krasik
 */
// TODO: Wrong JavaDoc
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ShellPath {
    /**
     * @return The path. Must be a valid path and use '/' as delimiters.<br>
     *         Any directory along the path that doesn't exist will be created.
     */
    String value() default "";

    /**
     * @return True if the element annotated with this annotation refers to a global path.<br>
     *         A global path means commands that are added to this path will be global commands.
     *         Global commands are commands that are not tied to the file system's hierarchy and are accessible
     *         from anywhere in the file system.<br>
     *         If this returns true, the value of {@link #value()} will be ignored.
     */
    boolean global() default false;
}

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
 * Indicates that the annotated method is a command.<br>
 * A command may receive any of the following parameters:
 * <ul>
 *     <li>{@link Boolean} or {@code boolean} parameters, optionally annotated with {@link BoolParam}</li>
 *     <li>{@link Double} or {@code double} parameters, optionally annotated with {@link DoubleParam}</li>
 *     <li>{@link Integer} or {@code int} parameters, optionally annotated with {@link IntParam}</li>
 *     <li>{@link String} parameters, optionally annotated with {@link StringParam}</li>
 * </ul>
 * <br>
 * Output may be written to the containing class's {@link CommandOutput}.<br>
 * If the command name is empty, the command will receive the name of the method.<br>
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * @return Command name. If empty, the method name will be used.
     */
    String value() default "";

    /**
     * @return Command description. If empty, a default description will be generated.
     */
    String description() default "";
}

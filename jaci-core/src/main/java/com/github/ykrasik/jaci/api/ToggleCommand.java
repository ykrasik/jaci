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
 * Indicates that the annotated method returns a toggle command.<br>
 * A toggle command is a command that takes a single optional boolean parameter and toggles
 * the boolean state of some component on or off. The state of the component is accessed via a {@link ToggleCommandStateAccessor}.<br>
 * <br>
 * Annotated methods must be no-args and return a {@link ToggleCommandStateAccessor}.<br>
 * Toggle commands can't have access to a {@link CommandOutput},
 * because toggle commands aren't meant to be general purpose commands.
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ToggleCommand {
    /**
     * @return Command name. If empty, the method name will be used.
     */
    String value() default "";

    /**
     * @return Command description.
     */
    String description() default "";

    /**
     * @return The command's single, optional boolean parameter name.
     */
    String paramName() default "";

    /**
     * @return The command's single, optional boolean parameter description.
     */
    String paramDescription() default "";
}

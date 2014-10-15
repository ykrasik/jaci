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
 * Indicates that this method returns a toggle command.<br>
 * A toggle command is a command that takes a single optional boolean parameter and toggles
 * the boolean state of some component on or off. The state of the component is accessed via a {@link com.github.ykrasik.jerminal.api.command.toggle.StateAccessor}.<br>
 * If the optional boolean parameter is passed, the toggle command will set the {@link com.github.ykrasik.jerminal.api.command.toggle.StateAccessor}'s
 * state to whatever value the parameter had. If boolean parameter is not passed, the toggle command
 * will toggle the state of the {@link com.github.ykrasik.jerminal.api.command.toggle.StateAccessor} - If it was previously 'false', it will now be 'true'
 * and vice versa.<br>
 * <br>
 * Methods annotated with this must return a {@link com.github.ykrasik.jerminal.api.command.toggle.StateAccessor}.<br>
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ToggleCommand {
    /**
     * @return Command description.
     */
    String value() default "command";
}

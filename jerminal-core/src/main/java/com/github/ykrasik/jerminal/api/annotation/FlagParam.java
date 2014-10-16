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
 * Indicates that this parameter is a flag parameter.<br>
 * Only applicable to boolean parameters, but is optional - any parameter not annotated will be
 * considered a mandatory parameter and will have a name and description generated for it.<br>
 * Flags are special optional boolean parameter that doesn't take values and are set to 'true'
 * if their name is present on the command line.<br>
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FlagParam {
    /**
     * @return Parameter name.
     */
    String value() default "";

    /**
     * @return Parameter description. If empty, a default description will be generated.
     */
    String description() default "";
}

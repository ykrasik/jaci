/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.reflection;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Reflection information about a field, through the Java reflection API.
 *
 * @author Yevgeny Krasik
 */
public class JavaReflectionField implements ReflectionField {
    private final Field field;

    public JavaReflectionField(Field field) {
        this.field = Objects.requireNonNull(field, "field");
    }

    @Override
    public Class<?> getType() { return field.getType(); }

    @Override
    public void setAccessible(boolean flag) { field.setAccessible(flag); }

    @Override
    public void set(Object obj, Object value) throws Exception { field.set(obj, value); }
}

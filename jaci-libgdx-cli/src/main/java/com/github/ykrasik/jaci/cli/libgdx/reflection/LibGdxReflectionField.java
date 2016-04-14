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

package com.github.ykrasik.jaci.cli.libgdx.reflection;

import com.badlogic.gdx.utils.reflect.Field;
import com.github.ykrasik.jaci.reflection.ReflectionField;

import java.util.Objects;

/**
 * Reflection information about a field, through the libGdx reflection API.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxReflectionField implements ReflectionField {
    private final Field field;

    public LibGdxReflectionField(Field field) {
        this.field = Objects.requireNonNull(field, "field");
    }

    @Override
    public Class<?> getType() {
        try {
            return field.getType();
        } catch (Exception e) {
            // Thrown by GWT when trying to get the type of a field for a class not in the reflection cache.
            // It's relatively safe to return null here, because the classes we're interested in are always in the
            // reflection cache, so this should just be ignored.
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public void setAccessible(boolean flag) { field.setAccessible(flag); }

    @Override
    public void set(Object obj, Object value) throws Exception { field.set(obj, value); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LibGdxReflectionField{");
        sb.append("field=").append(field);
        sb.append('}');
        return sb.toString();
    }
}

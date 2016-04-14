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

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.ykrasik.jaci.reflection.*;

import java.lang.annotation.Annotation;

/**
 * Accesses reflection through the libGdx API.
 *
 * @author Yevgeny Krasik
 */
public final class LibGdxReflectionAccessor implements ReflectionAccessor {
    private LibGdxReflectionAccessor() { }

    private static final ReflectionAccessor INSTANCE = new LibGdxReflectionAccessor();
    private static final Class<?>[] EMPTY_CLASSES = { };

    /**
     * Install this {@code ReflectionAccessor} to be used by the reflection API.
     */
    public static void install(){
        ReflectionUtils.setReflectionAccessor(INSTANCE);
    }

    @Override
    public ReflectionMethod getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws Exception {
        return new LibGdxReflectionMethod(ClassReflection.getDeclaredMethod(clazz, name, parameterTypes));
    }

    @Override
    public ReflectionMethod[] getMethods(Class<?> clazz) throws SecurityException {
        final Method[] methods = ClassReflection.getMethods(clazz);
        final ReflectionMethod[] reflectionMethods = new ReflectionMethod[methods.length];
        for (int i = 0; i < methods.length; i++) {
            reflectionMethods[i] = new LibGdxReflectionMethod(methods[i]);
        }
        return reflectionMethods;
    }

    @Override
    public ReflectionField[] getDeclaredFields(Class<?> clazz) throws SecurityException {
        final Field[] fields = ClassReflection.getDeclaredFields(clazz);
        final ReflectionField[] reflectionFields = new ReflectionField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            reflectionFields[i] = new LibGdxReflectionField(fields[i]);
        }
        return reflectionFields;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        final com.badlogic.gdx.utils.reflect.Annotation annotation = ClassReflection.getAnnotation(clazz, annotationClass);
        return annotation != null ? annotation.getAnnotation(annotationClass) : null;
    }

    @Override
    public <T> T newInstance(Class<T> clazz) throws Exception {
        return ClassReflection.newInstance(clazz);
    }

    @Override
    public Class<?>[] getDeclaredClasses(Class<?> clazz) throws Exception {
        // FIXME: Inner class-reflection is unsupported by libGdx :(
        return EMPTY_CLASSES;
    }

    @Override
    public <T> ReflectionConstructor<T> getDeclaredConstructor(Class<T> clazz,
                                                               Class<?>... parameterTypes) throws Exception {
        return new LibGdxReflectionConstructor<>(ClassReflection.getDeclaredConstructor(clazz, parameterTypes));
    }

    @Override
    public boolean isAssignableFrom(Class<?> c1, Class<?> c2) {
        return ClassReflection.isAssignableFrom(c1, c2);
    }
}

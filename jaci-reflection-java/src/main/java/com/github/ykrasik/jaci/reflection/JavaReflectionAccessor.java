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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Accesses reflection through the Java API.
 *
 * @author Yevgeny Krasik
 */
public final class JavaReflectionAccessor implements ReflectionAccessor {
    private JavaReflectionAccessor() { }

    private static final ReflectionAccessor INSTANCE = new JavaReflectionAccessor();

    /**
     * Install this {@code ReflectionAccessor} to be used by the reflection API.
     */
    public static void install(){
        ReflectionUtils.setReflectionAccessor(INSTANCE);
    }

    @Override
    public ReflectionMethod getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws Exception {
        return new JavaReflectionMethod(clazz.getDeclaredMethod(name, parameterTypes));
    }

    @Override
    public ReflectionMethod[] getMethods(Class<?> clazz) throws SecurityException {
        final Method[] methods = clazz.getMethods();
        final ReflectionMethod[] reflectionMethods = new ReflectionMethod[methods.length];
        for (int i = 0; i < methods.length; i++) {
            reflectionMethods[i] = new JavaReflectionMethod(methods[i]);
        }
        return reflectionMethods;
    }

    @Override
    public ReflectionField[] getDeclaredFields(Class<?> clazz) throws SecurityException {
        final Field[] fields = clazz.getDeclaredFields();
        final ReflectionField[] reflectionFields = new ReflectionField[fields.length];
        for (int i = 0; i < fields.length; i++) {
            reflectionFields[i] = new JavaReflectionField(fields[i]);
        }
        return reflectionFields;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    @Override
    public <T> T newInstance(Class<T> clazz) throws Exception {
        return clazz.newInstance();
    }

    @Override
    public Class<?>[] getDeclaredClasses(Class<?> clazz) throws Exception {
        return clazz.getDeclaredClasses();
    }

    @Override
    public <T> ReflectionConstructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) throws Exception {
        return new JavaReflectionConstructor<>(clazz.getDeclaredConstructor(parameterTypes));
    }
}

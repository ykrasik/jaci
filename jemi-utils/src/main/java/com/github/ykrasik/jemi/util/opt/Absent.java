/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.util.opt;

import com.github.ykrasik.jemi.util.function.Func;
import com.github.ykrasik.jemi.util.function.Pred;
import lombok.NonNull;

import java.util.*;

/**
 * An implementation of an {@code absent} {@link Opt}.
 *
 * @author Yevgeny Krasik
 */
@SuppressWarnings("unchecked")
final class Absent<T> extends Opt<T> {
    private static final long serialVersionUID = 0;
    private static final Absent<?> INSTANCE = new Absent<>();

    private Absent() { }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        throw new IllegalStateException("Called get() on an absent value!");
    }

    @Override
    public T getOrElseNull() {
        return null;
    }

    @Override
    public T getOrElse(T defaultValue) {
        return defaultValue;
    }

    @Override
    public Opt<T> orElse(@NonNull Opt<? extends T> alternative) {
        return (Opt<T>) alternative;
    }

    @Override
    public <V> Opt<V> map(Func<? super T, V> function) {
        return instance();
    }

    @Override
    public <V> Opt<V> flatMap(Func<? super T, Opt<V>> function) {
        return instance();
    }

    @Override
    public boolean exists(Pred<? super T> predicate) {
        return false;
    }

    @Override
    public Opt<T> filter(Pred<? super T> predicate) {
        return instance();
    }

    @Override
    public List<T> toList() {
        return Collections.emptyList();
    }

    @Override
    public Set<T> toSet() {
        return Collections.emptySet();
    }

    @Override
    public <K> Map<K, T> toMap(K key) {
        return Collections.emptyMap();
    }

    @Override
    public boolean equals(Object object) {
        return object == this;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "Absent";
    }

    private Object readResolve() {
        return INSTANCE;
    }

    static <T> Opt<T> instance() {
        return (Opt<T>) INSTANCE;
    }
}

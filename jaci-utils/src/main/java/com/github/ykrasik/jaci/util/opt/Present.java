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

package com.github.ykrasik.jaci.util.opt;

import com.github.ykrasik.jaci.util.function.Func;
import com.github.ykrasik.jaci.util.function.Pred;

import java.util.*;

/**
 * An implementation of a {@code present} {@link Opt}.
 *
 * @author Yevgeny Krasik
 */
final class Present<T> extends Opt<T> {
    private static final long serialVersionUID = 0;

    private final T value;

    Present(T value) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public T getOrElseNull() {
        return value;
    }

    @Override
    public T getOrElse(T defaultValue) {
        return value;
    }

    @Override
    public Opt<T> orElse(Opt<? extends T> alternative) {
        return this;
    }

    @Override
    public <V> Opt<V> map(Func<? super T, V> function) {
        return Opt.ofNullable(function.apply(value));
    }

    @Override
    public <V> Opt<V> flatMap(Func<? super T, Opt<V>> function) {
        return function.apply(value);
    }

    @Override
    public boolean exists(Pred<? super T> predicate) {
        return predicate.test(value);
    }

    @Override
    public Opt<T> filter(Pred<? super T> predicate) {
        return predicate.test(value) ? this : Opt.<T>absent();
    }

    @Override
    public List<T> toList() {
        return Collections.singletonList(value);
    }

    @Override
    public Set<T> toSet() {
        return Collections.singleton(value);
    }

    @Override
    public <K> Map<K, T> toMap(K key) {
        return Collections.singletonMap(key, value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Present<?>)) {
            return false;
        }

        final Present<?> other = (Present<?>) object;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "Present(" + value + ')';
    }
}

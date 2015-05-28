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

import com.github.ykrasik.jemi.util.function.Function;
import com.github.ykrasik.jemi.util.function.Predicate;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public abstract class Opt<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 0;

    protected Opt() { }

    public static <T> Opt<T> absent() {
        return Absent.instance();
    }

    public static <T> Opt<T> of(@NonNull T reference) {
        return new Present<>(reference);
    }

    public static <T> Opt<T> ofNullable(T value) {
        return (value == null) ? Opt.<T>absent() : new Present<>(value);
    }

    public static <T> Opt<T> fromCollection(Collection<T> col) {
        if (col.isEmpty()) {
            return absent();
        }
        if (col.size() == 1) {
            return of(col.iterator().next());
        }
        throw new IllegalArgumentException("Cannot create an Opt from a Collection with a size greater then 1!");
    }

    public abstract boolean isPresent();

    public abstract T get();
    public abstract T getOrElseNull();
    public abstract T getOrElse(T defaultValue);
    public abstract Opt<T> orElse(Opt<? extends T> alternative);

    public abstract <V> Opt<V> map(Function<? super T, V> function);
    public abstract <V> Opt<V> flatMap(Function<? super T, Opt<V>> function);

    public abstract boolean exists(Predicate<? super T> predicate);
    public abstract Opt<T> filter(Predicate<? super T> predicate);

    public abstract List<T> toList();
    public abstract Set<T> toSet();
    public abstract <K> Map<K, T> toMap(K key);

    @Override
    public final Iterator<T> iterator() {
        return toList().iterator();
    }
}

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

import java.io.Serializable;
import java.util.*;

/**
 * Just like Java 8's or Guava's Optional.
 * Uses a different name to avoid name confusion.
 *
 * @param <T> Argument type.
 *
 * @author Yevgeny Krasik
 */
public abstract class Opt<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 0;

    protected Opt() { }

    /**
     * @param <T> {@code Absent} value type.
     * @return An empty {@code Opt} instance that contains no value.
     */
    public static <T> Opt<T> absent() {
        return Absent.instance();
    }

    /**
     * @param value Value to wrap in an {@code Opt}, must be non-null.
     * @param <T> Value type.
     * @return A non-empty {@code Opt} instance with the given value.
     * @throws NullPointerException If value is null.
     */
    public static <T> Opt<T> of(@NonNull T value) {
        return new Present<>(value);
    }

    /**
     * @param value Possibly nullable value.
     * @param <T> Value type.
     * @return A non-empty {@code Opt} if the value is non-null, or an empty one otherwise.
     */
    public static <T> Opt<T> ofNullable(T value) {
        return (value == null) ? Opt.<T>absent() : new Present<>(value);
    }

    /**
     * Create an {@code Opt} instance out of a {@code Collection}. The collection's size may not be greater than 1.
     * Returns a non-empty {@code Opt} with the collection's only element if the collection is non-empty,
     * or an empty {@code Opt} otherwise.
     *
     * @param col Collection to extract an {@code Opt} out of. The collection's size may not be greater than 1.
     * @param <T> Collection type.
     * @return A non-empty {@code Opt} if the {@code Collection} has a single element, or an empty one otherwise.
     * @throws IllegalArgumentException If the collection's size is greater than 1.
     */
    public static <T> Opt<T> fromCollection(Collection<T> col) {
        if (col.isEmpty()) {
            return absent();
        }
        if (col.size() == 1) {
            return of(col.iterator().next());
        }
        throw new IllegalArgumentException("Cannot create an Opt from a Collection with a size greater then 1!");
    }

    /**
     * @return {@code true} if this {@code Opt} contains a value, otherwise {@code false}.
     */
    public abstract boolean isPresent();

    /**
     * @return This {@code Opt}'s non-null value.
     * @throws NoSuchElementException If this {@code Opt} is empty.
     */
    public abstract T get();

    /**
     * @return This {@code Opt}'s value if this {@code Opt} is non-empty, or {@code null} otherwise.
     */
    public abstract T getOrElseNull();

    /**
     * @param defaultValue Default value to return if this {@code Opt} is empty.
     * @return This {@code Opt}'s value if this {@code Opt} is non-empty, or {@code defaultValue} otherwise.
     */
    public abstract T getOrElse(T defaultValue);

    /**
     * @param alternative Alternative {@code Opt} to return if this {@code Opt} is empty.
     * @return This {@code Opt} if it is non-empty, or the alternative {@code Opt} otherwise.
     */
    public abstract Opt<T> orElse(Opt<? extends T> alternative);

    /**
     * @param function {@code Function} to apply to this {@code Opt}'s value, if this {@code Opt} is non-empty.
     *                 May return {@code null}, in which case an empty {@code Opt} will be returned.
     * @param <V> Returned {@code Opt} type.
     * @return An {@code Opt} containing the result of applying {@code function} to this {@code Opt}'s value
     *         if this {@code Opt} is non-empty, or an empty {@code Opt} otherwise.
     */
    public abstract <V> Opt<V> map(Func<? super T, V> function);

    /**
     * @param function {@code Function} to apply to this {@code Opt}'s value, if this {@code Opt} is non-empty.
     *                 Different from {@link #map(Func)} in that {@code function} must return an {@code Opt}.
     * @param <V> Returned {@code Opt} type.
     * @return The {@code Opt} result of applying {@code function} to this {@code Opt}'s value if this {@code Opt} is non-empty,
     *         or an empty {@code Opt} otherwise.
     */
    public abstract <V> Opt<V> flatMap(Func<? super T, Opt<V>> function);

    /**
     * @param predicate {@code Predicate} to apply to this {@code Opt}'s value if this {@code Opt} is non-empty.
     * @return {@code true} if this {@code Opt} is non-empty and applying the {@code predicate} to this {@code Opt}'s
     *         value returns {@code true}, otherwise returns {@code false}.
     */
    public abstract boolean exists(Pred<? super T> predicate);

    /**
     * @param predicate {@code Predicate} to apply to this {@code Opt}'s value if this {@code Opt} is non-empty.
     * @return This {@code Opt} if this {@code Opt} is non-empty and applying the predicate to it returns {@code true},
     *         otherwise returns an empty {@code Opt}.
     */
    public abstract Opt<T> filter(Pred<? super T> predicate);

    /**
     * @return A singleton {@code List} containing this {@code Opt}'s value if this {@code Opt} is non-empty,
     *         or an empty {@code List} otherwise.
     */
    public abstract List<T> toList();

    /**
     * @return A singleton {@code Set} containing this {@code Opt}'s value if this {@code Opt} is non-empty,
     *         or an empty {@code Set} otherwise.
     */
    public abstract Set<T> toSet();

    /**
     * @param key Key to use for mapping if this {@code Opt} is non-empty.
     * @param <K> Key type.
     * @return A singleton {@code Map} containing a single mapping from the given {@code key} to this {@code Opt}'s value
     *         if this {@code Opt} is non-empty, or an empty {@code Map} otherwise.
     */
    public abstract <K> Map<K, T> toMap(K key);

    @Override
    public final Iterator<T> iterator() {
        return toList().iterator();
    }
}

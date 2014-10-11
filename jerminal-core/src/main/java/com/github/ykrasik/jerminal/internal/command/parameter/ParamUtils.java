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

package com.github.ykrasik.jerminal.internal.command.parameter;

import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.Tries;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam}s.
 *
 * @author Yevgeny Krasik
 */
public final class ParamUtils {
    private ParamUtils() {
    }

    /**
     * @param value Value to be returned by the created {@link Supplier}.
     * @param <T> Supplier type.
     * @return A {@link Supplier} that always returns the value.
     */
    public static <T> Supplier<T> constValueSupplier(T value) {
        return Suppliers.ofInstance(Objects.requireNonNull(value));
    }

    /**
     * @param values Values to be returned by the created {@link Supplier}.
     * @return A {@link Supplier} that always returns a {@link Trie} containing the values.
     */
    public static Supplier<Trie<String>> constStringValuesSupplier(List<String> values) {
        final Trie<String> trie = Tries.toStringTrie(Objects.requireNonNull(values));
        return Suppliers.ofInstance(trie);
    }

    /**
     * @param supplier A {@link Supplier} that will supply a {@link List} of {@link String}.
     * @return A {@link Supplier} that returns a {@link Trie} containing the values of the
     *         {@link List} of {@link String} the supplier passed as an argument returned.
     */
    public static Supplier<Trie<String>> dynamicStringValuesSupplier(Supplier<List<String>> supplier) {
        return new DynamicStringValuesSupplier(supplier);
    }

    /**
     * A {@link Supplier} that queries another {@link Supplier} for a List of String, transforms
     * it into a {@link Trie} and returns that.
     */
    private static class DynamicStringValuesSupplier implements Supplier<Trie<String>> {
        private final Supplier<List<String>> supplier;

        private DynamicStringValuesSupplier(Supplier<List<String>> supplier) {
            this.supplier = checkNotNull(supplier, "supplier");
        }

        @Override
        public Trie<String> get() {
            final List<String> values = supplier.get();
            return Tries.toStringTrie(values);
        }
    }
}

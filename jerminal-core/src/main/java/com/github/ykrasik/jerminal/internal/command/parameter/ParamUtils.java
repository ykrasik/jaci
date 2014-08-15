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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam}s.
 *
 * @author Yevgeny Krasik
 */
public final class ParamUtils {
    private ParamUtils() {
    }

    public static <T> Supplier<T> constValueSupplier(T defaultValue) {
        return Suppliers.ofInstance(checkNotNull(defaultValue, "defaultValue"));
    }

    public static Supplier<Trie<String>> constStringValuesSupplier(List<String> possibleValues) {
        final Trie<String> trie = toTrie(checkNotNull(possibleValues, "possibleValues"));
        return Suppliers.ofInstance(trie);
    }

    public static Supplier<Trie<String>> dynamicStringValuesSupplier(Supplier<List<String>> supplier) {
        return new DynamicStringValuesSupplier(supplier);
    }

    private static class DynamicStringValuesSupplier implements Supplier<Trie<String>> {
        private final Supplier<List<String>> supplier;

        private DynamicStringValuesSupplier(Supplier<List<String>> supplier) {
            this.supplier = checkNotNull(supplier, "supplier");
        }

        @Override
        public Trie<String> get() {
            final List<String> values = supplier.get();
            return toTrie(values);
        }
    }

    private static Trie<String> toTrie(List<String> values) {
        final TrieBuilder<String> trieBuilder = new TrieBuilder<>();
        for (String value : values) {
            trieBuilder.add(value, value);
        }
        return trieBuilder.build();
    }
}

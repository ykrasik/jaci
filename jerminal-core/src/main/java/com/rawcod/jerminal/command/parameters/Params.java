package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 20:09
 */
public final class Params {
    private Params() {
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

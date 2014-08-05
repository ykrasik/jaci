package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.collections.trie.ReadOnlyTrie;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;

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


//    public static CommandParam stringParam(String name, String description, String... possibleValues) {
//        return stringParam(name, description, Arrays.asList(possibleValues));
//    }
//
//    public static CommandParam stringParam(String name, String description, List<String> possibleValues) {
//        return new StringParam(name, description, possibleValues);
//    }
//
//
//    public static CommandParam optionalStringParam(String name,
//                                                   String description,
//                                                   String defaultValue,
//                                                   String... possibleValues) {
//        return optionalStringParam(name, description, Arrays.asList(possibleValues), new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalStringParam(String name,
//                                                   String description,
//                                                   Supplier<String> defaultValueSupplier,
//                                                   String... possibleValues) {
//        return optionalStringParam(name, description, Arrays.asList(possibleValues), defaultValueSupplier);
//    }
//
//    public static CommandParam optionalStringParam(String name,
//                                                   String description,
//                                                   List<String> possibleValues,
//                                                   Supplier<String> defaultValueSupplier) {
//        final CommandParam stringParam = stringParam(name, description, possibleValues);
//        return new OptionalParam<>(stringParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam dynamicStringParam(String name, String description, Supplier<List<String>> valueSupplier) {
//        return new DynamicStringParam(name, description, valueSupplier);
//    }
//
//
//    public static CommandParam optionalDynamicStringParam(String name,
//                                                          String description,
//                                                          String defaultValue,
//                                                          Supplier<List<String>> valueSupplier) {
//        return optionalDynamicStringParam(name, description, valueSupplier, new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalDynamicStringParam(String name,
//                                                          String description,
//                                                          Supplier<List<String>> valueSupplier,
//                                                          Supplier<String> defaultValueSupplier) {
//        final CommandParam stringParam = dynamicStringParam(name, description, valueSupplier);
//        return new OptionalParam<>(stringParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam booleanParam(String name, String description) {
//        return new BooleanParam(name, description);
//    }
//
//
//    public static CommandParam optionalBooleanParam(String name, String description, boolean defaultValue) {
//        return optionalBooleanParam(name, description, new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalBooleanParam(String name, String description, Supplier<Boolean> defaultValueSupplier) {
//        final CommandParam booleanParam = booleanParam(name, description);
//        return new OptionalParam<>(booleanParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam integerParam(String name, String description) {
//        return new IntegerParam(name, description);
//    }
//
//
//    public static CommandParam optionalIntegerParam(String name, String description, int defaultValue) {
//        return optionalIntegerParam(name, description, new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalIntegerParam(String name, String description, Supplier<Integer> defaultValueSupplier) {
//        final CommandParam integerParam = integerParam(name, description);
//        return new OptionalParam<>(integerParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam doubleParam(String name, String description) {
//        return new DoubleParam(name, description);
//    }
//
//
//    public static CommandParam optionalDoubleParam(String name, String description, double defaultValue) {
//        return optionalDoubleParam(name, description, new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalDoubleParam(String name, String description, Supplier<Double> defaultValueSupplier) {
//        final CommandParam doubleParam = doubleParam(name, description);
//        return new OptionalParam<>(doubleParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam fileParam(String name, String description) {
//        return new FileParam(name, description);
//    }
//
//
//    public static CommandParam optionalFileParam(String name, String description, ShellCommand defaultValue) {
//        return optionalFileParam(name, description, new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalFileParam(String name, String description, Supplier<ShellCommand> defaultValueSupplier) {
//        final CommandParam fileParam = fileParam(name, description);
//        return new OptionalParam<>(fileParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam directoryParam(String name, String description) {
//        return new DirectoryParam(name, description);
//    }
//
//
//    public static CommandParam optionalDirectoryParam(String name, String description, ShellDirectory defaultValue) {
//        return optionalDirectoryParam(name, description, new ConstDefaultValueSupplier<>(defaultValue));
//    }
//
//    public static CommandParam optionalDirectoryParam(String name, String description, Supplier<ShellDirectory> defaultValueSupplier) {
//        final CommandParam directoryParam = directoryParam(name, description);
//        return new OptionalParam<>(directoryParam, defaultValueSupplier);
//    }
//
//
//    public static CommandParam flag(String name, String description) {
//        return new FlagParam(name, description);
//    }

    public static <T> Supplier<T> constValueSupplier(T defaultValue) {
        return new ConstDefaultValueSupplier<>(defaultValue);
    }

    public static Supplier<ReadOnlyTrie<String>> constStringValuesSupplier(List<String> possibleValues) {
        return new ConstStringValuesSupplier(possibleValues);
    }

    public static Supplier<ReadOnlyTrie<String>> dynamicStringValuesSupplier(Supplier<List<String>> supplier) {
        return new DynamicStringValuesSupplier(supplier);
    }

    private static class ConstDefaultValueSupplier<T> implements Supplier<T> {
        private final T defaultValue;

        private ConstDefaultValueSupplier(T defaultValue) {
            this.defaultValue = checkNotNull(defaultValue, "defaultValue");
        }

        @Override
        public T get() {
            return defaultValue;
        }
    }

    private static class ConstStringValuesSupplier implements Supplier<ReadOnlyTrie<String>> {
        private final Trie<String> trie;

        private ConstStringValuesSupplier(List<String> possibleValues) {
            this.trie = toTrie(checkNotNull(possibleValues, "possibleValues"));
        }

        @Override
        public Trie<String> get() {
            return trie;
        }
    }

    private static class DynamicStringValuesSupplier implements Supplier<ReadOnlyTrie<String>> {
        private final Supplier<List<String>> supplier;

        private DynamicStringValuesSupplier(Supplier<List<String>> supplier) {
            this.supplier = checkNotNull(supplier, "supplier");
        }

        @Override
        public ReadOnlyTrie<String> get() {
            final List<String> values = supplier.get();
            return toTrie(values);
        }
    }

    private static Trie<String> toTrie(List<String> values) {
        final Trie<String> trie = new TrieImpl<>();
        for (String value : values) {
            trie.put(value, value);
        }
        return trie;
    }
}

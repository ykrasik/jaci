package com.rawcod.jerminal.shell.entry.parameters;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public interface ShellParamDefaultValueProvider<V> {
    V getDefaultValue();

    class Const<V> implements ShellParamDefaultValueProvider<V> {
        private final V value;

        public Const(V value) {
            this.value = value;
        }

        @Override
        public V getDefaultValue() {
            return value;
        }
    }
}

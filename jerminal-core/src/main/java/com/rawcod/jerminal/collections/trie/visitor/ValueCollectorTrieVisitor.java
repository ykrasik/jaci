package com.rawcod.jerminal.collections.trie.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 21:50
 */
public class ValueCollectorTrieVisitor<T> implements TrieVisitor<T> {
    private final List<T> values;

    public ValueCollectorTrieVisitor() {
        this.values = new ArrayList<>();
    }

    @Override
    public void visit(String word, T value) {
        values.add(value);
    }

    public List<T> getValues() {
        return values;
    }
}

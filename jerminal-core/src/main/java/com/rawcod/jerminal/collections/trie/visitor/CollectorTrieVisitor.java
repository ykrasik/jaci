package com.rawcod.jerminal.collections.trie.visitor;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 21:49
 */
public class CollectorTrieVisitor<T> implements TrieVisitor<T> {
    private final Map<String, T> values;

    public CollectorTrieVisitor() {
        this.values = new HashMap<>();
    }

    @Override
    public void visit(String word, T value) {
        values.put(word, value);
    }

    public Map<String, T> getMap() {
        return values;
    }
}

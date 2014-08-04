package com.rawcod.jerminal.collections.trie.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 21:49
 */
public class CollectorTrieVisitor<T> implements TrieVisitor<T> {
    private final List<String> words;
    private final List<T> values;

    public CollectorTrieVisitor() {
        this.words = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    @Override
    public void visit(String word, T value) {
        words.add(word);
    }

    public List<String> getWords() {
        return words;
    }

    public List<T> getValues() {
        return values;
    }
}

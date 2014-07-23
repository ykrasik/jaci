package com.rawcod.jerminal.collections.trie.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 21:49
 */
public class WordCollectorTrieVisitor<T> implements TrieVisitor<T> {
    private final List<String> words;

    public WordCollectorTrieVisitor() {
        this.words = new ArrayList<>();
    }

    @Override
    public void visit(String word, T value) {
        words.add(word);
    }

    public List<String> getWords() {
        return words;
    }
}

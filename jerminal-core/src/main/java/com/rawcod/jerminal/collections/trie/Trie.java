package com.rawcod.jerminal.collections.trie;

import com.rawcod.jerminal.collections.trie.TrieFilter.NoTrieFilter;

import java.util.Collections;
import java.util.List;

public class Trie<V> {
    private static final TrieFilter NO_FILTER = new NoTrieFilter<>();

    private final TrieNode<V> root;

    public Trie() {
        root = new TrieNode<>();
    }

    public void addWord(String word, V value) {
        root.addWord(word, value);
    }

    public boolean isEmpty() {
        return root.isEmpty();
    }

    public V get(String word) {
        final TrieNode<V> node = getNode(word);
        if (node == null || !node.isWord()) {
            return null;
        }
        return node.getValue();
    }

    public List<String> getWords(String prefix) {
        return getWordsByFilter(prefix, NO_FILTER);
    }

    public List<String> getWordsByFilter(String prefix, TrieFilter<V> filter) {
        final TrieNode<V> node = getNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }
        return node.getWordsByFilter(filter);
    }

    public List<String> getAllWords() {
        return root.getWordsByFilter(NO_FILTER);
    }

    public List<String> getAllWordsByFilter(TrieFilter<V> filter) {
        return root.getWordsByFilter(filter);
    }

    public List<V> getValues(String prefix) {
        return getValuesByFilter(prefix, NO_FILTER);
    }

    public List<V> getValuesByFilter(String prefix, TrieFilter<V> filter) {
        final TrieNode<V> node = getNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }
        return node.getValuesByFilter(filter);
    }

    public List<V> getAllValues() {
        return root.getValuesByFilter(NO_FILTER);
    }

    public List<V> getAllValuesByFilter(TrieFilter filter) {
        return root.getValuesByFilter(filter);
    }

    public String getLongestPrefix(String prefix) {
        final TrieNode<V> node = getNode(prefix);
        if (node == null) {
            return "";
        }
        return node.getLongestPrefix();
    }

    private TrieNode<V> getNode(String word) {
        TrieNode<V> lastNode = root;
        for (int i = 0; i < word.length(); i++) {
            final TrieNode<V> child = lastNode.getNode(word.charAt(i));
            if (child == null) {
                return null;
            }
            lastNode = child;
        }

        return lastNode;
    }

    @Override
    public String toString() {
        return root.getAllWords().toString();
    }
}

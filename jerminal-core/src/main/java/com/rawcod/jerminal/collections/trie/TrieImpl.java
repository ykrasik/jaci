package com.rawcod.jerminal.collections.trie;

import java.util.Collections;
import java.util.List;

public class TrieImpl<V> implements Trie<V> {
    private final TrieNode<V> root;

    public TrieImpl() {
        root = new TrieNode<>();
    }

    @Override
    public void put(String word, V value) {
        root.addWord(word, value);
    }

    @Override
    public int size() {
        return root.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public V get(String word) {
        final TrieNode<V> node = getNode(word);
        if (node == null || !node.isWord()) {
            return null;
        }
        return node.getValue();
    }

    @Override
    public List<String> getAllWords() {
        return root.getAllWords();
    }

    @Override
    public List<String> getAllWordsByFilter(TrieFilter<V> filter) {
        return root.getWordsByFilter(filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getWords(String prefix) {
        return getWordsByFilter(prefix, (TrieFilter<V>) TrieNode.NO_FILTER);
    }

    @Override
    public List<String> getWordsByFilter(String prefix, TrieFilter<V> filter) {
        final TrieNode<V> node = getNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }
        return node.getWordsByFilter(filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<V> getAllValues() {
        return getAllValuesByFilter((TrieFilter<V>) TrieNode.NO_FILTER);
    }

    @Override
    public List<V> getAllValuesByFilter(TrieFilter<V> filter) {
        return root.getValuesByFilter(filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<V> getValues(String prefix) {
        return getValuesByFilter(prefix, (TrieFilter<V>) TrieNode.NO_FILTER);
    }

    @Override
    public List<V> getValuesByFilter(String prefix, TrieFilter<V> filter) {
        final TrieNode<V> node = getNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }
        return node.getValuesByFilter(filter);
    }

    @Override
    public String getLongestPrefix(String prefix) {
        final TrieNode<V> node = getNode(prefix);
        if (node == null) {
            return "";
        }
        return node.getLongestPrefix();
    }

    @Override
    public String getLongestExistingPrefix(String word) {
        TrieNode<V> lastNode = root;
        for (int i = 0; i < word.length(); i++) {
            final TrieNode<V> child = lastNode.getNode(word.charAt(i));
            if (child == null) {
                return lastNode.toString();
            }
            lastNode = child;
        }

        return lastNode.getLongestPrefix();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitAllWords(TrieVisitor<V> visitor) {
        visitWordsByFilter(visitor, (TrieFilter<V>) TrieNode.NO_FILTER);
    }

    @Override
    public void visitWordsByFilter(TrieVisitor<V> visitor, TrieFilter<V> filter) {
        root.visitWordsByFilter(visitor, filter);
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

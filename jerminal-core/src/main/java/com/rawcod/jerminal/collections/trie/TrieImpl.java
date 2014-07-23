package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.node.TrieNodeImpl;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;
import com.rawcod.jerminal.collections.trie.visitor.ValueCollectorTrieVisitor;
import com.rawcod.jerminal.collections.trie.visitor.WordCollectorTrieVisitor;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class TrieImpl<T> implements Trie<T> {
    private final TrieNode<T> root;
    private int numWords;

    public TrieImpl() {
        this(new TrieNodeImpl<T>());
    }

    private TrieImpl(TrieNode<T> root) {
        this.root = root;
    }

    @Override
    public int size() {
        return numWords;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public T get(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null ? node.getValue() : null;
    }

    @Override
    public List<String> getAllWords() {
        return getAllWordsWithFilter(Predicates.<T>alwaysTrue());
    }

    @Override
    public List<String> getAllWordsWithFilter(Predicate<T> filter) {
        return getWordsFromPrefixWithFilter("", filter);
    }

    @Override
    public List<String> getWordsFromPrefix(String prefix) {
        return getWordsFromPrefixWithFilter(prefix, Predicates.<T>alwaysTrue());
    }

    @Override
    public List<String> getWordsFromPrefixWithFilter(String prefix, Predicate<T> filter) {
        final TrieNode<T> node = getNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }

        final WordCollectorTrieVisitor<T> wordCollector = new WordCollectorTrieVisitor<>();
        visitWordsByFilterFromNode(node, filter, wordCollector);
        return wordCollector.getWords();
    }

    @Override
    public List<T> getAllValues() {
        return getAllValuesWithFilter(Predicates.<T>alwaysTrue());
    }

    @Override
    public List<T> getAllValuesWithFilter(Predicate<T> filter) {
        return getValuesByPrefixWithFilter("", filter);
    }

    @Override
    public List<T> getValuesByPrefix(String prefix) {
        return getValuesByPrefixWithFilter(prefix, Predicates.<T>alwaysTrue());
    }

    @Override
    public List<T> getValuesByPrefixWithFilter(String prefix, Predicate<T> filter) {
        final TrieNode<T> node = getNode(prefix);
        if (node == null) {
            return Collections.emptyList();
        }

        final ValueCollectorTrieVisitor<T> valueCollector = new ValueCollectorTrieVisitor<>();
        visitWordsByFilterFromNode(node, filter, valueCollector);
        return valueCollector.getValues();
    }

    @Override
    public void visitAllWords(TrieVisitor<T> visitor) {
        visitWordsByFilter(visitor, Predicates.<T>alwaysTrue());
    }

    @Override
    public void visitWordsByFilter(TrieVisitor<T> visitor, Predicate<T> filter) {
        visitWordsByFilterFromNode(root, filter, visitor);
    }

    @Override
    public String getLongestPrefix(String prefix) {
        // Get the node representing the prefix.
        final TrieNode<T> node = getNode(prefix);
        if (node == null) {
            // No node is reachable for this prefix, prefix is illegal.
            return "";
        }

        // Keep going down the tree, until a node has more then 1 children or is a word.
        TrieNode<T> currentNode = node;
        while (currentNode.numChildren() == 1 && !currentNode.isWord()) {
            // currentNode only has 1 child and is not a word.
            for (TrieNode<T> child : currentNode.getChildren()) {
                // Move on to currentNode's only child.
                currentNode = child;
            }
        }
        return currentNode.getPrefix();
    }

    @Override
    public ReadOnlyTrie<T> union(ReadOnlyTrie<T> other) {
        return null;
    }

    @Override
    public void put(String word, T value) {
        checkArgument(!word.isEmpty(), "Empty words are not allowed!");
        checkNotNull(value, "Null values are not allowed!");

        // Navigate the tree by the letters of the word, starting from the root.
        TrieNode<T> currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            final char c = word.charAt(i);
            currentNode = currentNode.getOrCreateChild(c);
        }

        final T oldValue = currentNode.setValue(value);
        checkState(oldValue == null, "Word '%s' already has a value: '%s'", word, oldValue);
        numWords++;
    }

    private TrieNode<T> getNode(String word) {
        // Navigate the tree by the letters of the word, starting from the root.
        TrieNode<T> currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            final char c = word.charAt(i);
            currentNode = currentNode.getChild(c);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode;
    }

    private void visitWordsByFilterFromNode(TrieNode<T> node,
                                            Predicate<T> filter,
                                            TrieVisitor<T> visitor) {
        // Visit the node, if it is accepted.
        visitIfNodeAccepted(node, filter, visitor);

        // Visit all the node's children.
        for (TrieNode<T> child : node.getChildren()) {
            visitWordsByFilterFromNode(child, filter, visitor);
        }
    }

    private void visitIfNodeAccepted(TrieNode<T> node, Predicate<T> filter, TrieVisitor<T> visitor) {
        final T value = node.getValue();
        if (node.isWord() && filter.apply(value)) {
            final String word = node.getPrefix();
            visitor.visit(word, value);
        }
    }

    @Override
    public String toString() {
        return getAllWords().toString();
    }
}

package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.node.TrieNodeImpl;
import com.rawcod.jerminal.collections.trie.node.UnionTrieNodeImpl;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;
import com.rawcod.jerminal.collections.trie.visitor.ValueCollectorTrieVisitor;
import com.rawcod.jerminal.collections.trie.visitor.WordCollectorTrieVisitor;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TrieImpl<T> implements Trie<T> {
    private final TrieNode<T> root;

    // This is the prefix of the current trie. Used by subTries.
    private final String triePrefix;

    public TrieImpl() {
        this(new TrieNodeImpl<T>(), "");
    }

    private TrieImpl(TrieNode<T> root, String triePrefix) {
        this.root = root;
        this.triePrefix = triePrefix;
    }

    @Override
    public boolean isEmpty() {
        return root.getChildren().isEmpty();
    }

    @Override
    public boolean contains(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null && isWord(node);
    }

    @Override
    public T get(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null ? node.getValue() : null;
    }

    @Override
    public List<String> getAllWords() {
        return getWordsWithFilter(Predicates.<T>alwaysTrue());
    }

    @Override
    public List<String> getWordsWithFilter(Predicate<T> filter) {
        final WordCollectorTrieVisitor<T> wordCollector = new WordCollectorTrieVisitor<>();
        visitWordsByFilter(wordCollector, filter);
        return wordCollector.getWords();
    }

    @Override
    public List<T> getAllValues() {
        return getValuesWithFilter(Predicates.<T>alwaysTrue());
    }

    @Override
    public List<T> getValuesWithFilter(Predicate<T> filter) {
        final ValueCollectorTrieVisitor<T> valueCollector = new ValueCollectorTrieVisitor<>();
        visitWordsByFilter(valueCollector, filter);
        return valueCollector.getValues();
    }

    @Override
    public void visitAllWords(TrieVisitor<T> visitor) {
        visitWordsByFilter(visitor, Predicates.<T>alwaysTrue());
    }

    @Override
    public void visitWordsByFilter(TrieVisitor<T> visitor, Predicate<T> filter) {
        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        visitWordsFromNodeByFilter(prefixBuilder, root, filter, visitor);
    }

    @Override
    public String getLongestPrefix() {
        // Keep going down the tree, until a node has more than 1 children or is a word.
        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        TrieNode<T> currentNode = root;
        while (currentNode.getChildren().size() == 1 && !isWord(currentNode)) {
            // currentNode only has 1 child and is not a word.
            prefixBuilder.append(currentNode.getCharacter());
            for (TrieNode<T> child : currentNode.getChildren()) {
                // Move on to currentNode's only child.
                currentNode = child;
            }
        }

        return prefixBuilder.toString();
    }

    @Override
    public ReadOnlyTrie<T> subTrie(String prefix) {
        final TrieNode<T> node = getNode(prefix);
        if (node == null) {
            return null;
        }
        return new TrieImpl<>(node, triePrefix + prefix);
    }

    @Override
    public ReadOnlyTrie<T> filter(Predicate<T> filter) {
        TrieNode<T> filteredRoot = filterNode(root, filter);
        if (filteredRoot == null) {
            // Empty root.
            filteredRoot = new TrieNodeImpl<>();
        }
        return new TrieImpl<>(filteredRoot, triePrefix);
    }

    private TrieNode<T> filterNode(TrieNode<T> node, Predicate<T> filter) {
        final T value = node.getValue();
        final boolean valueAccepted = value != null && filter.apply(value);

        // Filter the node's children.
        final List<TrieNode<T>> filteredChildren = filterChildren(node, filter);
        if (filteredChildren.isEmpty() && !valueAccepted) {
            // None of the node's children passed the filter,
            // and the node either had no value or it didn't pass the filter as well.
            // This node should be discarded.
            return null;
        }

        // Create a new node and set it's value if it passed the filter.
        final TrieNode<T> newNode = new TrieNodeImpl<>(node.getCharacter());
        if (valueAccepted) {
            newNode.setValue(value);
        }

        // Link the new node with the filtered children.
        for (TrieNode<T> child : filteredChildren) {
            newNode.setChild(child.getCharacter(), child);
        }

        return newNode;
    }

    private List<TrieNode<T>> filterChildren(TrieNode<T> node, Predicate<T> filter) {
        final Collection<TrieNode<T>> children = node.getChildren();
        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        final List<TrieNode<T>> filteredChildren = new ArrayList<>(children);
        for (TrieNode<T> child : children) {
            final TrieNode<T> filteredChild = filterNode(child, filter);
            if (filteredChild != null) {
                filteredChildren.add(filteredChild);
            }
        }
        return filteredChildren;
    }

    @Override
    public ReadOnlyTrie<T> union(ReadOnlyTrie<T> other) {
        // I couldn't find a better solution other then this downcasting...
        final TrieNode<T> unionRoot = new UnionTrieNodeImpl<>(root, ((TrieImpl<T>) other).root);
        return new TrieImpl<>(unionRoot, triePrefix);
    }

    @Override
    public void put(String word, T value) {
        checkArgument(!word.isEmpty(), "Empty words are not allowed!");
        checkNotNull(value, "Null values are not allowed!");

        // Navigate the tree by the letters of the word, starting from the root.
        TrieNode<T> currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            final char c = word.charAt(i);
            TrieNode<T> child = currentNode.getChild(c);
            if (child == null) {
                child = new TrieNodeImpl<>(c);
                currentNode.setChild(c, child);
            }
            currentNode = child;
        }

        final T currentValue = currentNode.getValue();
        if (currentValue != null) {
            final String message = String.format("Word '%s' already has a value: '%s'", word, currentValue);
            throw new IllegalStateException(message);
            // TODO: We've already inserted all the characters of the word. Remove them?
        }

        currentNode.setValue(value);
    }

    @Override
    public T remove(String word) {
        // Get the node representing the word.
        final TrieNode<T> node = getNode(word);
        if (node == null) {
            final String message = String.format("Trie does not contain word: '%s'", word);
            throw new IllegalArgumentException(message);
        }

        final T value = node.getValue();
        if (value == null) {
            final String message = String.format("Trie does not contain a value associated with word: '%s'", word);
            throw new IllegalArgumentException(message);
        }

        node.setValue(null);
        return value;
    }

    private void visitWordsFromNodeByFilter(StringBuilder prefixBuilder,
                                            TrieNode<T> node,
                                            Predicate<T> filter,
                                            TrieVisitor<T> visitor) {
        // Started processing node, push it's character to the prefix.
        if (node != root) {
            // The root node has no char.
            prefixBuilder.append(node.getCharacter());
        }

        // Visit the node, if it is accepted.
        visitIfNodeAccepted(prefixBuilder, node, filter, visitor);

        // Visit all the node's children.
        for (TrieNode<T> child : node.getChildren()) {
            visitWordsFromNodeByFilter(prefixBuilder, child, filter, visitor);
        }

        // Done processing node, pop it's character from the prefix.
        if (node != root && prefixBuilder.length() > 0) {
            prefixBuilder.deleteCharAt(prefixBuilder.length() - 1);
        }
    }

    private void visitIfNodeAccepted(StringBuilder prefixBuilder,
                                     TrieNode<T> node,
                                     Predicate<T> filter,
                                     TrieVisitor<T> visitor) {
        final T value = node.getValue();
        if (value != null && filter.apply(value)) {
            final String word = prefixBuilder.toString();
            visitor.visit(word, value);
        }
    }

    private TrieNode<T> getNode(String prefix) {
        // Navigate the tree by the letters of the prefix, starting from the root.
        TrieNode<T> currentNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            final char c = prefix.charAt(i);
            currentNode = currentNode.getChild(c);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode;
    }

    private boolean isWord(TrieNode<T> node) {
        return node.getValue() != null;
    }

    @Override
    public String toString() {
        return getAllWords().toString();
    }
}

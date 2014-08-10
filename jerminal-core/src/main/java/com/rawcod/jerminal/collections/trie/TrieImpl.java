package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.node.TrieNodeImpl;
import com.rawcod.jerminal.collections.trie.node.ValueTrieNode;
import com.rawcod.jerminal.collections.trie.visitor.CollectorTrieVisitor;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TrieImpl<T> implements Trie<T> {
    private static final Trie<?> EMPTY_TRIE = new TrieImpl<>(new TrieNodeImpl<>());

    private final ValueTrieNode<T> root;

    // This is the prefix of the current trie. Used by subTries.
    private final String triePrefix;

    TrieImpl(ValueTrieNode<T> root) {
        this(root, "");
    }

    private TrieImpl(ValueTrieNode<T> root, String triePrefix) {
        this.root = root;
        this.triePrefix = triePrefix;
    }

    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    public boolean contains(String word) {
        final ValueTrieNode<T> node = getNode(word);
        return node != null && node.isWord();
    }

    @Override
    public Optional<T> get(String word) {
        final ValueTrieNode<T> node = getNode(word);
        return node != null ? Optional.of(node.getValue()) : Optional.<T>absent();
    }

    @Override
    public List<String> getWords() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        final CollectorTrieVisitor<T> collector = new CollectorTrieVisitor<>();
        visitAllWords(collector);
        return collector.getWords();
    }

    @Override
    public List<T> getValues() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        final CollectorTrieVisitor<T> collector = new CollectorTrieVisitor<>();
        visitAllWords(collector);
        return collector.getValues();
    }

    @Override
    public void visitAllWords(TrieVisitor<T> visitor) {
        if (isEmpty()) {
            return;
        }

        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        visitWordsFromNodeByFilter(prefixBuilder, root, visitor);
    }

    private void visitWordsFromNodeByFilter(StringBuilder prefixBuilder,
                                            ValueTrieNode<T> node,
                                            TrieVisitor<T> visitor) {
        // Started processing node, push it's character to the prefix.
        if (node != root) {
            // The root node has no char.
            prefixBuilder.append(node.getCharacter());
        }

        // Visit the node, if it is accepted.
        visitIfNodeAccepted(prefixBuilder, node, visitor);

        // Visit all the node's children.
        for (TrieNode child : node.getChildren()) {
            visitWordsFromNodeByFilter(prefixBuilder, (ValueTrieNode<T>) child, visitor);
        }

        // Done processing node, pop it's character from the prefix.
        if (node != root && prefixBuilder.length() > 0) {
            prefixBuilder.deleteCharAt(prefixBuilder.length() - 1);
        }
    }

    private void visitIfNodeAccepted(StringBuilder prefixBuilder,
                                     ValueTrieNode<T> node,
                                     TrieVisitor<T> visitor) {
        final T value = node.getValue();
        if (value != null) {
            final String word = prefixBuilder.toString();
            visitor.visit(word, value);
        }
    }

    @Override
    public String getLongestPrefix() {
        if (isEmpty()) {
            return "";
        }

        // Keep going down the tree, until a node has more than 1 children or is a word.
        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        TrieNode currentNode = root;
        while (currentNode.getChildren().size() == 1 && !currentNode.isWord()) {
            // currentNode only has 1 child and is not a word.
            for (TrieNode child : currentNode.getChildren()) {
                // Move on to currentNode's only child.
                currentNode = child;

                // Append child's character to prefix.
                prefixBuilder.append(currentNode.getCharacter());
            }
        }

        return prefixBuilder.toString();
    }

    @Override
    public Trie<T> subTrie(String prefix) {
        if (isEmpty()) {
            return this;
        }

        final ValueTrieNode<T> node = getNode(prefix);
        if (node == null) {
            return emptyTrie();
        }
        return new TrieImpl<>(node, triePrefix + prefix);
    }

    @Override
    public Trie<T> filter(final Predicate<T> filter) {
        return map(new Function<T, T>() {
            @Override
            public T apply(T input) {
                return filter.apply(input) ? input : null;
            }
        });
    }

    @Override
    public <A> Trie<A> map(Function<T, A> function) {
        final ValueTrieNode<A> newRoot = mapNode(root, function);
        if (newRoot == null) {
            // Empty root.
            return emptyTrie();
        }
        return new TrieImpl<>(newRoot, triePrefix);
    }

    private <A> ValueTrieNode<A> mapNode(ValueTrieNode<T> node, Function<T, A> function) {
        final T value = node.getValue();
        final A newValue = value != null ? function.apply(value) : null;

        // Map the node's children.
        final List<ValueTrieNode<A>> newChildren = mapChildren(node, function);
        if (newChildren.isEmpty() && newValue == null) {
            // None of the node's mapped to actual values,
            // and the node either had no value or it didn't map to a value as well.
            // This node should be discarded.
            return null;
        }

        // Create a new node and set it's value.
        final ValueTrieNode<A> newNode = new TrieNodeImpl<>(node.getCharacter());
        newNode.setValue(newValue);

        // Link the new node with the mapped children.
        for (ValueTrieNode<A> child : newChildren) {
            newNode.setChild(child.getCharacter(), child);
        }

        return newNode;
    }

    private <A> List<ValueTrieNode<A>> mapChildren(ValueTrieNode<T> node, Function<T, A> function) {
        final Collection<TrieNode> children = node.getChildren();
        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ValueTrieNode<A>> newChildren = new ArrayList<>(children.size());
        for (TrieNode child : children) {
            final ValueTrieNode<A> newChild = mapNode((ValueTrieNode<T>) child, function);
            if (newChild != null) {
                newChildren.add(newChild);
            }
        }
        return newChildren;
    }

    private ValueTrieNode<T> getNode(String prefix) {
        // Navigate the tree by the letters of the prefix, starting from the root.
        ValueTrieNode<T> currentNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            final char c = prefix.charAt(i);
            currentNode = (ValueTrieNode<T>) currentNode.getChild(c);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode;
    }

    @SuppressWarnings("unchecked")
    public static <T> Trie<T> emptyTrie() {
        return (Trie<T>) EMPTY_TRIE;
    }

    @Override
    public String toString() {
        return getWords().toString();
    }
}

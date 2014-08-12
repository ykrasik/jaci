package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.node.TrieNodeBuilder;
import com.rawcod.jerminal.collections.trie.node.TrieNodeImpl;
import com.rawcod.jerminal.collections.trie.visitor.CollectorTrieVisitor;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;
import com.rawcod.jerminal.exception.ShellException;

import java.util.*;

public class TrieImpl<T> implements Trie<T> {
    private static final Trie<?> EMPTY_TRIE = new TrieImpl<>(new TrieNodeBuilder<>().build());

    private final TrieNode<T> root;

    // This is the prefix of the current trie. Used by subTries.
    private final String triePrefix;

    public TrieImpl(TrieNode<T> root) {
        this(root, "");
    }

    private TrieImpl(TrieNode<T> root, String triePrefix) {
        this.root = root;
        this.triePrefix = triePrefix;
    }

    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    public TrieNode<T> getRoot() {
        return root;
    }

    @Override
    public boolean contains(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null && node.isWord();
    }

    @Override
    public Optional<T> get(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null ? Optional.fromNullable(node.getValue()) : Optional.<T>absent();
    }

    @Override
    public Collection<String> getWords() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        final CollectorTrieVisitor<T> collector = new CollectorTrieVisitor<>();
        visitWords(collector);
        return collector.getMap().keySet();
    }

    @Override
    public Collection<T> getValues() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        final CollectorTrieVisitor<T> collector = new CollectorTrieVisitor<>();
        visitWords(collector);
        return collector.getMap().values();
    }

    @Override
    public void visitWords(TrieVisitor<T> visitor) {
        if (isEmpty()) {
            return;
        }

        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        visitWordsFromNodeByFilter(prefixBuilder, root, visitor);
    }

    private void visitWordsFromNodeByFilter(StringBuilder prefixBuilder,
                                            TrieNode<T> node,
                                            TrieVisitor<T> visitor) {
        // Started processing node, push it's character to the prefix.
        if (node != root) {
            // The root node has no char.
            prefixBuilder.append(node.getCharacter());
        }

        // Visit the node, if it is accepted.
        visitIfNodeAccepted(prefixBuilder, node, visitor);

        // Visit all the node's children.
        for (TrieNode<T> child : node.getChildren()) {
            visitWordsFromNodeByFilter(prefixBuilder, child, visitor);
        }

        // Done processing node, pop it's character from the prefix.
        if (node != root && prefixBuilder.length() > 0) {
            prefixBuilder.deleteCharAt(prefixBuilder.length() - 1);
        }
    }

    private void visitIfNodeAccepted(StringBuilder prefixBuilder,
                                     TrieNode<T> node,
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
        TrieNode<T> currentNode = root;
        while (currentNode.getChildren().size() == 1 && !currentNode.isWord()) {
            // currentNode only has 1 child and is not a word.
            for (TrieNode<T> child : currentNode.getChildren()) {
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

        final TrieNode<T> node = getNode(prefix);
        if (node == null) {
            return emptyTrie();
        }
        return new TrieImpl<>(node, triePrefix + prefix);
    }

    @Override
    public <A> Trie<A> map(Function<T, A> function) {
        if (isEmpty()) {
            return emptyTrie();
        }

        final TrieNode<A> newRoot = mapNode(root, function);
        if (newRoot == null) {
            // Empty root.
            return emptyTrie();
        }
        return new TrieImpl<>(newRoot, triePrefix);
    }

    private <A> TrieNode<A> mapNode(TrieNode<T> node, Function<T, A> function) {
        final T value = node.getValue();
        final A newValue = value != null ? function.apply(value) : null;

        // Map the node's children.
        final Map<Character, TrieNode<A>> newChildren = mapChildren(node, function);
        if (newChildren.isEmpty() && newValue == null) {
            // None of the node's mapped to actual values,
            // and the node either had no value or it didn't map to a value as well.
            // This node should be discarded.
            return null;
        }

        // Create a new node.
        return new TrieNodeImpl<>(node.getCharacter(), newValue, newChildren);
    }

    private <A> Map<Character, TrieNode<A>> mapChildren(TrieNode<T> node, Function<T, A> function) {
        final Collection<TrieNode<T>> children = node.getChildren();
        if (children.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Character, TrieNode<A>> newChildren = new HashMap<>(children.size());
        for (TrieNode<T> child : children) {
            final TrieNode<A> newChild = mapNode(child, function);
            if (newChild != null) {
                newChildren.put(child.getCharacter(), newChild);
            }
        }
        return newChildren;
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
    public Trie<T> union(Trie<T> other) {
        if (isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }

        final TrieNode<T> unionRoot = createUnionNode(root, other.getRoot());
        return new TrieImpl<>(unionRoot, triePrefix);
    }

    private TrieNode<T> createUnionNode(TrieNode<T> mainNode, TrieNode<T> otherNode) {
        final char character = mainNode.getCharacter();
        final char otherCharacter = otherNode.getCharacter();
        if (character != otherCharacter) {
            throw new ShellException("Trying to create a union between incompatible nodes: '%s' and '%s'!", character, otherCharacter);
        }

        final T unionValue = createUnionValue(mainNode, otherNode);
        final Map<Character, TrieNode<T>> unionChildren = createUnionChildren(mainNode, otherNode);
        return new TrieNodeImpl<>(character, unionValue, unionChildren);
    }

    private T createUnionValue(TrieNode<T> mainNode, TrieNode<T> otherNode) {
        T value = mainNode.getValue();
        if (value == null) {
            value = otherNode.getValue();
        }
        return value;
    }

    private Map<Character, TrieNode<T>> createUnionChildren(TrieNode<T> mainNode, TrieNode<T> otherNode) {
        // Check which of node1's children are also present in node2 and vice versa.
        // Those that are unique will be used as is.
        // Those that are present in both will be replaced with a UnionNode.
        final Map<Character, TrieNode<T>> unionChildren = new HashMap<>(mainNode.getChildren().size() + otherNode.getChildren().size());
        checkNode(mainNode, otherNode, unionChildren);
        checkNode(otherNode, mainNode, unionChildren);
        return unionChildren;
    }

    private void checkNode(TrieNode<T> mainNode,
                           TrieNode<T> otherNode,
                           Map<Character, TrieNode<T>> unionChildren) {
        for (TrieNode<T> mainChild : mainNode.getChildren()) {
            final char character = mainChild.getCharacter();
            if (unionChildren.containsKey(character)) {
                // This node's character was already handled in a previous iteration.
                continue;
            }

            final TrieNode<T> otherChild = otherNode.getChild(character);
            final TrieNode<T> trieNodeToAdd;
            if (otherChild == null) {
                // The other node has no child under 'c'.
                trieNodeToAdd = mainChild;
            } else {
                // The other node has a child under 'c', use a union node.
                trieNodeToAdd = createUnionNode(mainChild, otherChild);
            }
            unionChildren.put(character, trieNodeToAdd);
        }
    }

    @Override
    public Map<String, T> toMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        final CollectorTrieVisitor<T> collector = new CollectorTrieVisitor<>();
        visitWords(collector);
        return collector.getMap();
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

    @SuppressWarnings("unchecked")
    public static <T> Trie<T> emptyTrie() {
        return (Trie<T>) EMPTY_TRIE;
    }

    @Override
    public String toString() {
        return getWords().toString();
    }
}

package com.rawcod.jerminal.collections.trie.node;

import com.google.common.collect.Sets;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 22:52
 */
public class UnionTrieNodeImpl<T> implements TrieNode<T> {
    private final TrieNode<T> node1;
    private final TrieNode<T> node2;

    private Map<Character, TrieNode<T>> children;

    public UnionTrieNodeImpl(TrieNode<T> node1, TrieNode<T> node2) {
        checkArgument(!(node1.isWord() && node2.isWord()), "Cannot create a node union between 2 word nodes!");

        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public int numChildren() {
        return lazyGetChildren().size();
    }

    @Override
    public boolean isEmpty() {
        return numChildren() == 0;
    }

    @Override
    public char getCharacter() {
        return node1.getCharacter();
    }

    @Override
    public String getPrefix() {
        return node1.getPrefix();
    }

    @Override
    public boolean isWord() {
        return node1.isWord() || node2.isWord();
    }

    @Override
    public T getValue() {
        if (node1.isWord()) {
            return node1.getValue();
        }
        if (node2.isWord()) {
            return node2.getValue();
        }
        return null;
    }

    @Override
    public T setValue(T value) {
        throw new UnsupportedOperationException("Union nodes are read-only!");
    }

    @Override
    public TrieNode<T> getChild(char c) {
        final Map<Character, TrieNode<T>> children = lazyGetChildren();
        TrieNode<T> child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return child;
    }

    @Override
    public void setChild(char c, TrieNode<T> child) {
        throw new UnsupportedOperationException("Union nodes are read-only!");
    }

    @Override
    public Iterable<TrieNode<T>> getChildren() {
        return lazyGetChildren().values();
    }

    private Map<Character, TrieNode<T>> lazyGetChildren() {
        if (children == null) {
            children = createUnionChildren();
        }
        return children;
    }

    private Map<Character, TrieNode<T>> createUnionChildren() {
        final Map<Character, TrieNode<T>> unionChildren = new HashMap<>(node1.numChildren());

        // Check which of node1's children are also present in node2 and vice versa.
        // Those that are unique will be used as is.
        // Those that are present in both will be replaced with a UnionNode.

        // Iterate over node1's children.
        final List<TrieNode<T>> commonNodes = checkChildren(node1.getChildren(), node2, unionChildren);

        // Avoid checking children that are present in both nodes twice.
        final Set<TrieNode<T>> uncheckedNodes2 = Sets.newHashSet(node2.getChildren());
        uncheckedNodes2.removeAll(commonNodes);

        // Iterate over node2's still unchecked children
        checkChildren(uncheckedNodes2, node1, unionChildren);

        return unionChildren;
    }

    private List<TrieNode<T>> checkChildren(Iterable<TrieNode<T>> children,
                                            TrieNode<T> otherNode,
                                            Map<Character, TrieNode<T>> unionChildren) {
        final List<TrieNode<T>> commonNodes = new ArrayList<>();
        for (TrieNode<T> child : children) {
            final char c = child.getCharacter();
            final TrieNode<T> otherChild = otherNode.getChild(c);
            final TrieNode<T> unionNode;
            if (otherChild == null) {
                // otherNode doesn't have a child under the character 'c', use the original child.
                unionNode = child;
            } else {
                // Both nodes have a child under the character 'c', use a union node.
                unionNode = new UnionTrieNodeImpl<>(child, otherChild);
                commonNodes.add(otherChild);
            }
            unionChildren.put(c, unionNode);
        }
        return commonNodes;
    }

    @Override
    public String toString() {
        return getPrefix();
    }
}

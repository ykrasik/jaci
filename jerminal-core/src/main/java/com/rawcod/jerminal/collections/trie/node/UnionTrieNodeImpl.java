package com.rawcod.jerminal.collections.trie.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 22:52
 */
public class UnionTrieNodeImpl implements TrieNode {
    private final TrieNode node1;
    private final TrieNode node2;

    private Map<Character, TrieNode> children;

    public UnionTrieNodeImpl(TrieNode node1, TrieNode node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public char getCharacter() {
        return node1.getCharacter();
    }

    @Override
    public boolean isWord() {
        return node1.isWord() || node2.isWord();
    }

    @Override
    public TrieNode getChild(char c) {
        final Map<Character, TrieNode> children = lazyGetChildren();
        TrieNode child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return child;
    }

    @Override
    public Collection<TrieNode> getChildren() {
        return lazyGetChildren().values();
    }

    private Map<Character, TrieNode> lazyGetChildren() {
        if (children == null) {
            children = createUnionChildren();
        }
        return children;
    }

    private Map<Character, TrieNode> createUnionChildren() {
        // Check which of node1's children are also present in node2 and vice versa.
        // Those that are unique will be used as is.
        // Those that are present in both will be replaced with a UnionNode.
        final Map<Character, TrieNode> unionChildren = new HashMap<>(node1.getChildren().size() + node2.getChildren().size());
        checkNode(node1, node2, unionChildren);
        checkNode(node2, node1, unionChildren);
        return unionChildren;
    }

    private void checkNode(TrieNode mainNode,
                           TrieNode otherNode,
                           Map<Character, TrieNode> unionChildren) {
        for (TrieNode mainChild : mainNode.getChildren()) {
            final char character = mainChild.getCharacter();
            if (unionChildren.containsKey(character)) {
                // This node's character was already handled in a previous iteration.
                continue;
            }

            final TrieNode otherChild = otherNode.getChild(character);
            final TrieNode trieNodeToAdd;
            if (otherChild == null) {
                // The other node has no child under 'c'.
                trieNodeToAdd = mainChild;
            } else {
                // The other node has a child under 'c', use a union node.
                trieNodeToAdd = new UnionTrieNodeImpl(mainChild, otherChild);
            }
            unionChildren.put(character, trieNodeToAdd);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(node1.getCharacter());
    }
}

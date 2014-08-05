package com.rawcod.jerminal.collections.trie;

import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.node.UnionTrieNodeImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 22:45
 */
public class TrieViewImpl implements TrieView {
    private final TrieNode root;

    // This is the prefix of the current trie. Used by subTries.
    private final String triePrefix;

    protected TrieViewImpl(TrieNode root, String triePrefix) {
        this.root = root;
        this.triePrefix = triePrefix;
    }

    @Override
    public boolean isEmpty() {
        return !root.isWord() && root.getChildren().isEmpty();
    }

    @Override
    public boolean contains(String word) {
        final TrieNode node = getNode(word);
        return node != null && node.isWord();
    }

    @Override
    public List<String> getAllWords() {
        final List<String> words = new ArrayList<>();
        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        collectWordsFromNode(words, prefixBuilder, root);
        return words;
    }

    @Override
    public String getLongestPrefix() {
        // Keep going down the tree, until a node has more than 1 children or is a word.
        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);

        TrieNode currentNode = root;
        while (currentNode.getChildren().size() == 1 && !currentNode.isWord()) {
            // currentNode only has 1 child and is not a word.
            if (currentNode != root) {
                prefixBuilder.append(currentNode.getCharacter());
            }
            for (TrieNode child : currentNode.getChildren()) {
                // Move on to currentNode's only child.
                currentNode = child;
            }
        }

        return prefixBuilder.toString();
    }

    @Override
    public TrieView subTrie(String prefix) {
        final TrieNode node = getNode(prefix);
        if (node == null) {
            return null;
        }
        return new TrieViewImpl(node, triePrefix + prefix);
    }

    @Override
    public TrieView union(TrieView other) {
        // I couldn't find a better solution other then this downcasting...
        final TrieNode unionRoot = new UnionTrieNodeImpl(root, ((TrieViewImpl) other).root);
        return new TrieViewImpl(unionRoot, triePrefix);
    }

    private void collectWordsFromNode(List<String> words,
                                      StringBuilder prefixBuilder,
                                      TrieNode node) {
        // Started processing node, push it's character to the prefix.
        if (node != root) {
            // Skip the root node's char, already added by the prefix.
            prefixBuilder.append(node.getCharacter());
        }

        if (node.isWord()) {
            words.add(prefixBuilder.toString());
        }

        // Check node's children.
        for (TrieNode child : node.getChildren()) {
            collectWordsFromNode(words, prefixBuilder, child);
        }

        // Done processing node, pop it's character from the prefix.
        if (node != root && prefixBuilder.length() > 0) {
            prefixBuilder.deleteCharAt(prefixBuilder.length() - 1);
        }
    }

    private TrieNode getNode(String prefix) {
        // Navigate the tree by the letters of the prefix, starting from the root.
        TrieNode currentNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            final char c = prefix.charAt(i);
            currentNode = currentNode.getChild(c);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode;
    }

    @Override
    public String toString() {
        return getAllWords().toString();
    }
}

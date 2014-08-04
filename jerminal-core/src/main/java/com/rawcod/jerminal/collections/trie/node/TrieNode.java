package com.rawcod.jerminal.collections.trie.node;

import java.util.Collection;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 20:26
 */
public interface TrieNode {
    char getCharacter();
    boolean isWord();

    TrieNode getChild(char c);
    Collection<TrieNode> getChildren();
}

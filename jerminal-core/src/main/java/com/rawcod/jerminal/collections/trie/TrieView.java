package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Optional;

import java.util.List;

/**
 * User: ykrasik
 * Date: 29/07/2014
 * Time: 21:25
 */
public interface TrieView {
    boolean isEmpty();
    boolean contains(String word);

    List<String> getAllWords();
    String getLongestPrefix();

    Optional<TrieView> subTrie(String prefix);
    TrieView union(TrieView other);
}

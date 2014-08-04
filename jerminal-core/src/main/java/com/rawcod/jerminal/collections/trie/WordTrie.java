package com.rawcod.jerminal.collections.trie;

import java.util.List;

/**
 * User: ykrasik
 * Date: 29/07/2014
 * Time: 21:25
 */
public interface WordTrie {
    boolean isEmpty();
    boolean contains(String word);

    List<String> getAllWords();
    String getLongestPrefix();

    WordTrie subTrie(String prefix);
    WordTrie union(WordTrie other);
}

package com.rawcod.jerminal.shell.parser;

import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.filesystem.entry.ShellAutoComplete;

import java.util.List;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ShellWordContainer<V> {
    private final TrieImpl<V> words;

    public ShellWordContainer() {
        this.words = new TrieImpl<>();
    }

    public void addWord(String name, V value) {
        words.put(name, value);
    }

    public V get(String name) {
        return words.get(name);
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public List<V> getAllValues() {
        return words.getAllValues();
    }

    public ShellAutoComplete autoComplete(String arg, TrieFilter<V> filter) {
        final List<String> possibleValues = words.getWordsByFilter(arg, filter);

        // Couldn't match any child entry.
        if (possibleValues.isEmpty()) {
            return ShellAutoComplete.none();
        }

        // Matched a single child entry.
        if (possibleValues.size() == 1) {
            final String matchedArg = possibleValues.get(0);
            return ShellAutoComplete.single(matchedArg);
        }

        // Matched multiple potential child words
        final String longestPrefix = words.getLongestPrefix(arg);
        return ShellAutoComplete.multiple(longestPrefix, possibleValues);
    }
}

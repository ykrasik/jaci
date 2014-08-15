package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Objects;
import com.github.ykrasik.jerminal.collections.trie.Trie;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 22/07/2014
 * Time: 20:44
 */
public class AutoCompleteReturnValue {
    private final String prefix;
    private final Trie<AutoCompleteType> possibilities;

    public AutoCompleteReturnValue(String prefix, Trie<AutoCompleteType> possibilities) {
        this.prefix = checkNotNull(prefix, "prefix");
        this.possibilities = checkNotNull(possibilities, "possibilities");
    }

    public String getPrefix() {
        return prefix;
    }

    public Trie<AutoCompleteType> getPossibilities() {
        return possibilities;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("prefix", prefix)
            .add("possibilities", possibilities)
            .toString();
    }
}
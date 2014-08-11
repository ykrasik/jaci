package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Objects;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 22/07/2014
 * Time: 20:44
 */
public class AutoCompleteReturnValue extends ReturnValueImpl<AutoCompleteReturnValueSuccess, AutoCompleteReturnValueFailure> {
    private AutoCompleteReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static AutoCompleteReturnValue success(String prefix, Trie<AutoCompleteType> possibilities) {
        return new AutoCompleteReturnValue(new AutoCompleteReturnValueSuccess(prefix, possibilities));
    }

    public static AutoCompleteReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompleteReturnValue(failure);
    }


    public static class AutoCompleteReturnValueSuccess extends SuccessImpl {
        private final String prefix;
        private final Trie<AutoCompleteType> possibilities;

        private AutoCompleteReturnValueSuccess(String prefix, Trie<AutoCompleteType> possibilities) {
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
}
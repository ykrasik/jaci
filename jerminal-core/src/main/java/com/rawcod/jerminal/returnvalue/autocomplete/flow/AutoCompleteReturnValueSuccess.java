package com.rawcod.jerminal.returnvalue.autocomplete.flow;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 13:05
 */
public class AutoCompleteReturnValueSuccess extends ReturnValueImpl.SuccessImpl {
    private final String autoCompleteAddition;
    private final List<String> possibilities;

    public AutoCompleteReturnValueSuccess(String autoCompleteAddition, List<String> possibilities) {
        this.autoCompleteAddition = checkNotNull(autoCompleteAddition, "autoCompleteAddition is null!");
        this.possibilities = checkNotNull(possibilities, "possibilities is null!");
    }

    public String getAutoCompleteAddition() {
        return autoCompleteAddition;
    }

    public List<String> getPossibilities() {
        return possibilities;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("autoCompleteAddition", autoCompleteAddition)
            .add("possibilities", possibilities)
            .toString();
    }
}

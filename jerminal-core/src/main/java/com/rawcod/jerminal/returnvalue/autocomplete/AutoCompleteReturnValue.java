package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.SuccessImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;

import java.util.Collections;
import java.util.List;

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


    public static AutoCompleteReturnValue successSingle(String autoCompleteAddition) {
        return new AutoCompleteReturnValue(new AutoCompleteReturnValueSuccess(autoCompleteAddition, Collections.singletonList(autoCompleteAddition)));
    }

    public static AutoCompleteReturnValue successMultiple(String autoCompleteAddition, List<String> possibilities) {
        if (possibilities.size() < 2) {
            throw new IllegalArgumentException("Multiple autoComplete must have at least 2 suggestions!");
        }
        return new AutoCompleteReturnValue(new AutoCompleteReturnValueSuccess(autoCompleteAddition, possibilities));
    }

    public static AutoCompleteReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompleteReturnValue(failure);
    }


    public static class AutoCompleteReturnValueSuccess extends SuccessImpl {
        private final String autoCompleteAddition;
        private final List<String> suggestions;

        private AutoCompleteReturnValueSuccess(String autoCompleteAddition, List<String> suggestions) {
            this.autoCompleteAddition = checkNotNull(autoCompleteAddition, "autoCompleteAddition is null!");
            this.suggestions = checkNotNull(suggestions, "suggestions is null!");
        }

        public String getAutoCompleteAddition() {
            return autoCompleteAddition;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                .add("autoCompleteAddition", autoCompleteAddition)
                .add("suggestions", suggestions)
                .toString();
        }
    }
}
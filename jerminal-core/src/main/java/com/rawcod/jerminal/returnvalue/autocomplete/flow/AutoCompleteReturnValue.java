package com.rawcod.jerminal.returnvalue.autocomplete.flow;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.Failable;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 08/01/14
 */
public class AutoCompleteReturnValue extends ReturnValueImpl<AutoCompleteReturnValueSuccess, AutoCompleteReturnValueFailure> {
    private AutoCompleteReturnValue(Failable returnValue) {
        super(returnValue);
    }


    public static AutoCompleteReturnValue successSingle(String newPath) {
        return successMultiple(newPath, Collections.<String>emptyList());
    }

    public static AutoCompleteReturnValue successMultiple(String newPath, List<String> possibilities) {
        return new AutoCompleteReturnValue(new AutoCompleteReturnValueSuccess(newPath, possibilities));
    }

    public static AutoCompleteReturnValue failure(AutoCompleteReturnValueFailure failure) {
        return new AutoCompleteReturnValue(failure);
    }

    public static AutoCompleteReturnValue parseFailure(ParseReturnValueFailure failure) {
        return new AutoCompleteReturnValue(AutoCompleteReturnValueFailure.parseFailure(failure));
    }


    public static class AutoCompleteReturnValueSuccess extends SuccessImpl {
        private final String autoCompleteAddition;
        private final List<String> possibilities;

        private AutoCompleteReturnValueSuccess(String autoCompleteAddition, List<String> possibilities) {
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
}

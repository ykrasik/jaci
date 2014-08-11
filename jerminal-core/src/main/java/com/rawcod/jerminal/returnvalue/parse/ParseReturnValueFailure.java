package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.FailureImpl;
import com.rawcod.jerminal.returnvalue.suggestion.Suggestions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 23:43
 */
public class ParseReturnValueFailure extends FailureImpl {
    private final ParseError error;
    private final String errorMessage;
    private final Optional<Suggestions> suggestions;

    public ParseReturnValueFailure(ParseError error, String errorMessage, Optional<Suggestions> suggestions) {
        this.error = checkNotNull(error, "error");
        this.errorMessage = checkNotNull(errorMessage, "errorMessage");
        this.suggestions = checkNotNull(suggestions, "suggestions");
    }

    public ParseError getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Optional<Suggestions> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("errorMessage", errorMessage)
            .add("suggestions", suggestions)
            .toString();
    }
}

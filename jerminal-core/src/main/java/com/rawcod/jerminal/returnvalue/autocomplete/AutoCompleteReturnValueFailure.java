package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.FailureImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.suggestion.Suggestions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 13:14
 */
public class AutoCompleteReturnValueFailure extends FailureImpl {
    private final AutoCompleteError error;
    private final Optional<ParseError> parseError;
    private final String errorMessage;
    private final Optional<Suggestions> suggestions;

    public AutoCompleteReturnValueFailure(AutoCompleteError error,
                                          Optional<ParseError> parseError,
                                          String errorMessage,
                                          Optional<Suggestions> suggestions) {
        this.error = checkNotNull(error, "error");
        this.parseError = checkNotNull(parseError, "parseError");
        this.errorMessage = checkNotNull(errorMessage, "errorMessage");
        this.suggestions = checkNotNull(suggestions, "suggestions");
    }

    public AutoCompleteError getError() {
        return error;
    }

    public Optional<ParseError> getParseError() {
        return parseError;
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
            .add("parseError", parseError)
            .add("errorMessage", errorMessage)
            .add("suggestions", suggestions)
            .toString();
    }
}

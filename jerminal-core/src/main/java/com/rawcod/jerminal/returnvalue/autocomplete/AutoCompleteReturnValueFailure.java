package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 13:14
 */
public class AutoCompleteReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final AutoCompleteError error;
    private final Optional<ParseError> parseError;
    private final String message;
    private final List<String> suggestions;

    public AutoCompleteReturnValueFailure(AutoCompleteError error,
                                          Optional<ParseError> parseError,
                                          String message,
                                          List<String> suggestions) {
        this.error = checkNotNull(error, "error is null!");
        this.parseError = checkNotNull(parseError, "parseError is null!");
        this.message = checkNotNull(message, "message is null!");
        this.suggestions = checkNotNull(suggestions, "suggestions is null!");
    }

    public AutoCompleteError getError() {
        return error;
    }

    public Optional<ParseError> getParseError() {
        return parseError;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("parseError", parseError)
            .add("message", message)
            .add("suggestions", suggestions)
            .toString();
    }
}

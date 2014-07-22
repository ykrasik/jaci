package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

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
    private final ShellSuggestion suggestion;

    public AutoCompleteReturnValueFailure(AutoCompleteError error,
                                          Optional<ParseError> parseError,
                                          String message,
                                          ShellSuggestion suggestion) {
        this.error = checkNotNull(error, "error is null!");
        this.parseError = checkNotNull(parseError, "parseError is null!");
        this.message = checkNotNull(message, "message is null!");
        this.suggestion = checkNotNull(suggestion, "suggestion is null!");
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

    public ShellSuggestion getSuggestion() {
        return suggestion;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("parseError", parseError)
            .add("message", message)
            .add("suggestion", suggestion)
            .toString();
    }

    public static AutoCompleteReturnValueFailure from(AutoCompleteError error, String format, Object... args) {
        return from(error, ShellSuggestion.none(), format, args);
    }

    public static AutoCompleteReturnValueFailure from(AutoCompleteError error,
                                                      ShellSuggestion suggestion,
                                                      String format, Object... args) {
        final String message = String.format(format, args);
        return new AutoCompleteReturnValueFailure(error, Optional.<ParseError>absent(), message, suggestion);
    }

    public static AutoCompleteReturnValueFailure parseFailure(ParseReturnValueFailure failure) {
        return new AutoCompleteReturnValueFailure(
            AutoCompleteError.PARSE_ERROR,
            Optional.of(failure.getError()),
            failure.getMessage(),
            failure.getSuggestion()
        );
    }

    public static AutoCompleteReturnValueFailure internalError(String format, Object... args) {
        return from(AutoCompleteError.INTERNAL_ERROR, format, args);
    }
}

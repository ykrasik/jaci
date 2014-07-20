package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.rawcod.jerminal.filesystem.entry.ShellSuggestion;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 23:43
 */
public class ParseReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ParseError error;
    private final String message;
    private final ShellSuggestion suggestion;

    public ParseReturnValueFailure(ParseError error, String message, ShellSuggestion suggestion) {
        this.error = checkNotNull(error, "error is null!");
        this.message = checkNotNull(message, "message is null!");
        this.suggestion = checkNotNull(suggestion, "suggestion is null!");
    }

    public ParseError getError() {
        return error;
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
            .add("message", message)
            .add("suggestion", suggestion)
            .toString();
    }

    public static ParseReturnValueFailure from(ParseError error, String format, Object... args) {
        return from(error, ShellSuggestion.none(), format, args);
    }

    public static ParseReturnValueFailure from(ParseError error, ShellSuggestion suggestion, String format, Object... args) {
        final String message = String.format(format, args);
        return new ParseReturnValueFailure(error, message, suggestion);
    }

    public static ParseReturnValueFailure emptyDirectory(String directoryName) {
        return from(ParseError.EMPTY_DIRECTORY, "Parse error: Directory '%s' is empty.", directoryName);
    }
}

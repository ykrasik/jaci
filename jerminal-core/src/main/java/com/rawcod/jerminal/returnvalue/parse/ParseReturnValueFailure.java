package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 23:43
 */
public class ParseReturnValueFailure extends ReturnValueImpl.FailureImpl {
    private final ParseError error;
    private final String message;
    private final List<String> suggestions;

    public ParseReturnValueFailure(ParseError error, String message, List<String> suggestions) {
        this.error = checkNotNull(error, "error is null!");
        this.message = checkNotNull(message, "message is null!");
        this.suggestions = checkNotNull(suggestions, "suggestions is null!");
    }

    public ParseError getError() {
        return error;
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
            .add("message", message)
            .add("suggestions", suggestions)
            .toString();
    }

    public static ParseReturnValueFailure from(ParseError error, String format, Object... args) {
        return from(error, Collections.<String>emptyList(), format, args);
    }

    public static ParseReturnValueFailure from(ParseError error, List<String> suggestions, String format, Object... args) {
        final String message = String.format(format, args);
        return new ParseReturnValueFailure(error, message, suggestions);
    }

    public static ParseReturnValueFailure emptyDirectory(String directoryName) {
        return from(
            ParseError.EMPTY_DIRECTORY,
            "Parse error: Directory '%s' is empty.", directoryName
        );
    }

    public static ParseReturnValueFailure invalidParam(String paramName) {
        return from(
            ParseError.INVALID_PARAM,
            "Parse error: Invalid param: '%s'", paramName
        );
    }

    public static ParseReturnValueFailure paramValueNotSpecified(String paramName) {
        return from(
            ParseError.PARAM_VALUE_NOT_SPECIFIED,
            "Parse error: Value not specified for param: '%s'", paramName
        );
    }

    public static ParseReturnValueFailure InvalidParamValue(String paramName, String value) {
        return from(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Invalid value for param '%s': '%s'", paramName, value
        );
    }

    public static ParseReturnValueFailure paramAlreadyBound(String paramName, Object value) {
        return from(
            ParseError.PARAM_ALREADY_BOUND,
            "Parse error: Param '%s' is already bound to a value: '%s", paramName, value
        );
    }
}

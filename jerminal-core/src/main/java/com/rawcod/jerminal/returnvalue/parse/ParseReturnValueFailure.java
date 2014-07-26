package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Objects;
import com.rawcod.jerminal.returnvalue.ReturnValueImpl;

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
}

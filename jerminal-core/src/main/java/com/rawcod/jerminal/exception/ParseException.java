package com.rawcod.jerminal.exception;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.suggestion.Suggestions;

/**
 * User: ykrasik
 * Date: 13/08/2014
 * Time: 20:54
 */
public class ParseException extends Exception {
    private final ParseError error;
    private final Optional<Suggestions> suggestions;

    public ParseException(ParseError error, String message, Optional<Suggestions> suggestions) {
        super(message);
        this.error = error;
        this.suggestions = suggestions;
    }

    public ParseException(ParseError error, Optional<Suggestions> suggestions, String format, Object... args) {
        this(error, String.format(format, args), suggestions);
    }

    public ParseException(ParseError error, String message) {
        this(error, message, Optional.<Suggestions>absent());
    }

    public ParseException(ParseError error, String format, Object... args) {
        this(error, Optional.<Suggestions>absent(), format, args);
    }

    public ParseError getError() {
        return error;
    }

    public Optional<Suggestions> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("error", error)
            .add("message", getMessage())
            .add("suggestions", suggestions)
            .toString();
    }
}

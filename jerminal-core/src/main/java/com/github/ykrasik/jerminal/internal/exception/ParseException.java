/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.exception;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.github.ykrasik.jerminal.internal.returnvalue.Suggestions;

/**
 * An exception that signifies an error while parsing the command line.
 *
 * @author Yevgeny Krasik
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

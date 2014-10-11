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

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.google.common.base.Optional;

import java.util.Objects;

/**
 * An exception that signifies an error while parsing the command line.
 *
 * @author Yevgeny Krasik
 */
public class ParseException extends Exception {
    private final ParseError error;
    private final Optional<CommandInfo> commandInfo;

    public ParseException(ParseError error, String message) {
        this(message, error, Optional.<CommandInfo>absent());
    }

    public ParseException(ParseError error, String format, Object... args) {
        this(String.format(format, args), error, Optional.<CommandInfo>absent());
    }

    private ParseException(String message, ParseError error, Optional<CommandInfo> commandInfo) {
        super(message);
        this.error = Objects.requireNonNull(error);
        this.commandInfo = commandInfo;
    }

    public ParseException withCommandInfo(CommandInfo commandInfo) {
        return new ParseException(getMessage(), error, Optional.of(commandInfo));
    }

    public ParseError getError() {
        return error;
    }

    public Optional<CommandInfo> getCommandInfo() {
        return commandInfo;
    }

    @Override
    public String toString() {
        return "ParseException{" + "message=" + getMessage() + ", error=" + error + ", commandInfo=" + commandInfo + '}';
    }
}

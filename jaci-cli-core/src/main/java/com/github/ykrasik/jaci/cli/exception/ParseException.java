/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.cli.exception;

import com.github.ykrasik.jaci.cli.assist.CommandInfo;
import com.github.ykrasik.jaci.util.opt.Opt;
import lombok.NonNull;
import lombok.ToString;

/**
 * An exception that signals an error while parsing the command line.
 *
 * @author Yevgeny Krasik
 */
// TODO: Split this into 2 exceptions: CommandParseException, ParamParseException? will save the optional<commandInfo>.
@ToString
public class ParseException extends Exception {
    private final ParseError error;
    private final Opt<CommandInfo> commandInfo;

    public ParseException(ParseError error, String message) {
        this(message, error, Opt.<CommandInfo>absent());
    }

    public ParseException(ParseError error, String format, Object... args) {
        this(String.format(format, args), error, Opt.<CommandInfo>absent());
    }

    private ParseException(@NonNull String message,
                           @NonNull ParseError error,
                           @NonNull Opt<CommandInfo> commandInfo) {
        super(message);
        this.error = error;
        this.commandInfo = commandInfo;
    }

    /**
     * Add command info to this exception.
     *
     * @param commandInfo Command info to add.
     * @return A copy of this exception with command info added.
     */
    // TODO: This is never called.
    public ParseException withCommandInfo(CommandInfo commandInfo) {
        return new ParseException(getMessage(), error, Opt.of(commandInfo));
    }

    /**
     * @return The parse error.
     */
    public ParseError getError() {
        return error;
    }

    /**
     * @return The command info.
     */
    public Opt<CommandInfo> getCommandInfo() {
        return commandInfo;
    }
}

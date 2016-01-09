/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.cli.assist;

import com.github.ykrasik.jaci.cli.command.CliCommand;

import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
// FIXME: This class may be redundant... Either actually attach it to ParseExceptions or remove it.
public class CommandInfo {
    private final CliCommand command;
    private final BoundParams boundParams;

    public CommandInfo(CliCommand command, BoundParams boundParams) {
        this.command = Objects.requireNonNull(command, "command");
        this.boundParams = Objects.requireNonNull(boundParams, "boundParams");
    }

    public CliCommand getCommand() {
        return command;
    }

    public BoundParams getBoundParams() {
        return boundParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CommandInfo that = (CommandInfo) o;

        if (!command.equals(that.command)) {
            return false;
        }
        return boundParams.equals(that.boundParams);

    }

    @Override
    public int hashCode() {
        int result = command.hashCode();
        result = 31 * result + boundParams.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommandInfo{");
        sb.append("command=").append(command);
        sb.append(", boundParams=").append(boundParams);
        sb.append('}');
        return sb.toString();
    }
}

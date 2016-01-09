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

package com.github.ykrasik.jaci.command;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An implementation of a {@link CommandArgs}.
 * Popping args is implemented by maintaining an index that can only be increased.
 *
 * @author Yevgeny Krasik
 */
public class CommandArgsImpl implements CommandArgs {
    private final List<Object> args;
    private int index = 0;

    /**
     * @param args Parsed command args.
     */
    public CommandArgsImpl(List<Object> args) {
        this.args = Objects.requireNonNull(args, "args");
    }

    @Override
    public List<Object> getArgs() {
        return Collections.unmodifiableList(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T popArg() {
        if (index >= args.size()) {
            throw new IllegalArgumentException("No more arguments!");
        }

        final Object value = args.get(index);
        index++;
        return (T) value;
    }
}

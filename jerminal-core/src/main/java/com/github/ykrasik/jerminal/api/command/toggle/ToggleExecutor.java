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

package com.github.ykrasik.jerminal.api.command.toggle;

import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;

import java.util.Objects;

/**
 * A {@link CommandExecutor} that sets the value of it's {@link StateAccessor} according to input.
 */
public class ToggleExecutor implements CommandExecutor {
    private final String name;
    private final StateAccessor accessor;

    public ToggleExecutor(String name, StateAccessor accessor) {
        this.name = Objects.requireNonNull(name);
        this.accessor = Objects.requireNonNull(accessor);
    }

    @Override
    public void execute(CommandArgs args, OutputPrinter outputPrinter) throws Exception {
        final boolean toggle = args.popBool();
        accessor.set(toggle);
        outputPrinter.println("%s: %s", name, toggle);
    }
}

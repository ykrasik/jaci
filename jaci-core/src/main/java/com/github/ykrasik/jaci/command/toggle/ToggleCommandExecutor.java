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

package com.github.ykrasik.jaci.command.toggle;

import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.command.CommandExecutor;
import lombok.NonNull;

/**
 * A {@link CommandExecutor} that sets the value of it's {@link ToggleCommandStateAccessor} according to input.
 */
public class ToggleCommandExecutor implements CommandExecutor {
    private final String name;
    private final ToggleCommandStateAccessor accessor;

    public ToggleCommandExecutor(@NonNull String name, @NonNull ToggleCommandStateAccessor accessor) {
        this.name = name;
        this.accessor = accessor;
    }

    @Override
    public void execute(CommandOutput output, CommandArgs args) throws Exception {
        final boolean toggle = args.popArg();
        accessor.set(toggle);
        output.message("%s: %s", name, toggle);
    }
}

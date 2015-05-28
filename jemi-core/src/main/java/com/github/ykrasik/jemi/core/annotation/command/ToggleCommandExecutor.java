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

package com.github.ykrasik.jemi.core.annotation.command;

import com.github.ykrasik.jemi.api.CommandOutput;
import com.github.ykrasik.jemi.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jemi.core.command.CommandArgs;
import com.github.ykrasik.jemi.core.command.CommandExecutor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A {@link CommandExecutor} that sets the value of it's {@link ToggleCommandStateAccessor} according to input.
 */
@RequiredArgsConstructor
public class ToggleCommandExecutor implements CommandExecutor {
    @NonNull private final String name;
    @NonNull private final ToggleCommandStateAccessor accessor;

    @Override
    public void execute(CommandOutput output, CommandArgs args) throws Exception {
        final boolean toggle = args.popBool();
        accessor.set(toggle);
        output.message("%s: %s", name, toggle);
    }
}

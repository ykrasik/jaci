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

import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.google.common.base.Supplier;

import java.util.Objects;

/**
 * Creates toggle {@link com.github.ykrasik.jerminal.api.filesystem.command.Command Commands}.<br>
 * A toggle command is a command that takes a single optional boolean parameter and toggles
 * the boolean state of some component on or off. The state of the component is accessed via a {@link StateAccessor}.<br>
 * If the optional boolean parameter is passed, the toggle command will set the {@link StateAccessor}'s
 * state to whatever value the parameter had. If boolean parameter is not passed, the toggle command
 * will toggle the state of the {@link StateAccessor} - If it was previously 'false', it will now be 'true'
 * and vice versa.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class ToggleCommandBuilder {
    private final String commandName;

    private StateAccessor accessor;
    private String commandDescription = "Toggle command";
    private String paramName = "state";
    private String paramDescription = "toggle";

    public ToggleCommandBuilder(String commandName) {
        this.commandName = Objects.requireNonNull(commandName);
    }

    // TODO: JavaDoc
    public Command build() {
        Objects.requireNonNull(accessor, "StateAccessor wasn't defined!");
        return new CommandBuilder(commandName)
            .setDescription(commandDescription)
            .addParam(
                new BooleanParamBuilder(paramName)
                    .setDescription(paramDescription)
                    .setOptional(new AccessorDefaultValueProvider(accessor))
                    .build()
            )
            .setExecutor(new ToggleExecutor(commandName, accessor))
            .build();
    }

    // TODO: JavaDoc
    public ToggleCommandBuilder setAccessor(StateAccessor accessor) {
        this.accessor = accessor;
        return this;
    }

    // TODO: JavaDoc
    public ToggleCommandBuilder setCommandDescription(String description) {
        this.commandDescription = description;
        return this;
    }

    // TODO: JavaDoc
    public ToggleCommandBuilder setParamName(String paramName) {
        this.paramName = paramName;
        return this;
    }

    // TODO: JavaDoc
    public ToggleCommandBuilder setParamDescription(String description) {
        this.paramDescription = description;
        return this;
    }

    /**
     * A {@link Supplier> that returns the inverse of the current {@link StateAccessor#get()}.
     */
    private static class AccessorDefaultValueProvider implements Supplier<Boolean> {
        private final StateAccessor accessor;

        private AccessorDefaultValueProvider(StateAccessor accessor) {
            this.accessor = accessor;
        }

        @Override
        public Boolean get() {
            return !accessor.get();
        }
    }
}

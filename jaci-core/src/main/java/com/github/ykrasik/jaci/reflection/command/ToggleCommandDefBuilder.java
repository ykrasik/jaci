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

package com.github.ykrasik.jaci.reflection.command;

import com.github.ykrasik.jaci.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.param.BooleanParamDef;
import lombok.NonNull;

/**
 * Creates toggle {@link CommandDef}s.<br>
 * A toggle command is a command that takes a single optional boolean parameter and toggles
 * the boolean state of some component on or off. The state of the component is accessed via a {@link ToggleCommandStateAccessor}.<br>
 * If the optional boolean parameter is passed, the toggle command will set the {@link ToggleCommandStateAccessor}'s
 * state to whatever value the parameter had. If boolean parameter is not passed, the toggle command
 * will toggle the state of the {@link ToggleCommandStateAccessor} -
 * If it was previously {@code false}, it will now be {@code true} and vice versa.
 *
 * @author Yevgeny Krasik
 */
public class ToggleCommandDefBuilder {
    private final String name;
    private final ToggleCommandStateAccessor accessor;

    private String description = "toggle command";
    private String paramName = "state";
    private String paramDescription = "toggle";

    /**
     * @param name Command name.
     * @param accessor Accessor the the command's boolean state component.
     */
    public ToggleCommandDefBuilder(@NonNull String name, @NonNull ToggleCommandStateAccessor accessor) {
        this.name = name;
        this.accessor = accessor;
    }

    /**
     * Set the description of the command.
     *
     * @param description Description to set.
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    /**
     * Set the single optional boolean parameter name.
     *
     * @param paramName Name to set.
     */
    public void setParamName(@NonNull String paramName) {
        this.paramName = paramName;
    }

    /**
     * Set the single optional boolean parameter description.
     *
     * @param paramDescription Description to set.
     */
    public void setParamDescription(@NonNull String paramDescription) {
        this.paramDescription = paramDescription;
    }

    /**
     * @return A toggle {@link CommandDef} built out of this builder's parameters.
     */
    public CommandDef build() {
        final BooleanParamDef param = new BooleanParamDef.Builder(paramName)
            .setDescription(paramDescription)
            .setOptional(new ToggleCommandAccessorDefaultValueSupplier(accessor))
            .build();

        return new CommandDef.Builder(name, new ToggleCommandExecutor(name, accessor))
            .setDescription(description)
            .addParam(param)
            .build();
    }
}

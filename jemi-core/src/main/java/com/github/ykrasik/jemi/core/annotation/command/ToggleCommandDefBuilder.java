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

package com.github.ykrasik.jemi.core.annotation.command;

import com.github.ykrasik.jemi.api.ToggleCommandStateAccessor;
import com.github.ykrasik.jemi.core.command.CommandDef;
import com.github.ykrasik.jemi.core.param.BooleanParamDef;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Creates toggle {@link CommandDef}s.<br>
 * A toggle command is a command that takes a single optional boolean parameter and toggles
 * the boolean state of some component on or off. The state of the component is accessed via a {@link ToggleCommandStateAccessor}.<br>
 * If the optional boolean parameter is passed, the toggle command will set the {@link ToggleCommandStateAccessor}'s
 * state to whatever value the parameter had. If boolean parameter is not passed, the toggle command
 * will toggle the state of the {@link ToggleCommandStateAccessor} - If it was previously 'false', it will now be 'true'
 * and vice versa.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@Accessors(chain = true)
public class ToggleCommandDefBuilder {
    private final String name;

    @Setter
    @NonNull private String description = "toggle command";

    @Setter
    @NonNull private String paramName = "state";

    @Setter
    @NonNull private String paramDescription = "toggle";

    @Setter
    @NonNull private ToggleCommandStateAccessor accessor;

    public ToggleCommandDefBuilder(@NonNull String name) {
        this.name = name;
    }

    public CommandDef build() {
        final BooleanParamDef param = new BooleanParamDef.Builder(paramName)
            .setDescription(paramDescription)
            .setOptional(new ToggleCommandAccessorDefaultValueSupplier(accessor))
            .build();

        return new CommandDef.Builder(name)
            .setDescription(description)
            .addParam(param)
            .setExecutor(new ToggleCommandExecutor(name, accessor))
            .build();
    }
}

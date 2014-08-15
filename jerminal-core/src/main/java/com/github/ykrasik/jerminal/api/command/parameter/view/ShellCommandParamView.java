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

package com.github.ykrasik.jerminal.api.command.parameter.view;

import com.github.ykrasik.jerminal.internal.Describable;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamType;

/**
 * A view of a {@link com.github.ykrasik.jerminal.api.command.ShellCommand command}'s
 * {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam parameter}.
 *
 * @author Yevgeny Krasik
 */
public interface ShellCommandParamView extends Describable {
    /**
     * Returns the type of this parameter.
     */
    ParamType getType();

    /**
     * Returns the external form representation of this parameter.
     */
    String getExternalForm();
}

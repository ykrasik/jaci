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

import com.github.ykrasik.jaci.api.CommandOutput;

import java.util.Objects;

/**
 * A {@link CommandOutput} that promises to eventually contain a concrete implementation.
 * Delegates all calls to that concrete implementation.
 * This will be injected into objects expecting a {@link CommandOutput}, for the Annotation API.
 *
 * @author Yevgeny Krasik
 */
public class CommandOutputPromise implements CommandOutput {
    private CommandOutput output;

    /**
     * Set the concrete {@link CommandOutput} implementation to delegate to.
     *
     * @param output Concrete implementation to delegate to.
     */
    public void setOutput(CommandOutput output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    @Override
    public void message(String text) {
        output.message(text);
    }

    @Override
    public void error(String text) {
        output.error(text);
    }
}

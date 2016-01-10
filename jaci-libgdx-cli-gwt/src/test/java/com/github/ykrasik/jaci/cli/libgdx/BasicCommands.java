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

package com.github.ykrasik.jaci.cli.libgdx;

import com.github.ykrasik.jaci.api.*;

/**
 * @author Yevgeny Krasik
 */
public class BasicCommands {
    // This field will be injected by Jaci when the class is processed.
    private CommandOutput output;

    @Command
    public void paramExample(@IntParam("mandatoryInt") int intParam,
                             @StringParam(value = "optionalString", optional = true, defaultValueSupplier = "defaultSupplier") String stringParam,
                             @BoolParam(value = "flag", optional = true, defaultValue = false) boolean flag) {
        output.message("mandatoryInt="+intParam+", optionalString="+stringParam+", flag=" + flag);
    }

    private String defaultSupplier() {
        // Should be replaced by a real calculation
        return "suppliedDefault";
    }

    @Command(description = "Display 'Hello, World!'")
    public void helloWorld() {
        output.message("Hello, World!");
    }

    @ToggleCommand(description = "Toggles something")
    public ToggleCommandStateAccessor toggle2() {
        return new ToggleCommandStateAccessor() {
            private boolean state;

            @Override
            public void set(boolean value) {
                state = value;
            }

            @Override
            public boolean get() {
                return state;
            }
        };
    }
}

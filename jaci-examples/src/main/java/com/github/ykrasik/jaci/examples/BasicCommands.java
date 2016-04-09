/******************************************************************************
 * Copyright (c) 2016 Yevgeny Krasik.                                         *
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

package com.github.ykrasik.jaci.examples;

import com.github.ykrasik.jaci.api.*;

/**
 * Some basic command usage.
 *
 * @author Yevgeny Krasik
 */
public class BasicCommands {
    // This field will be injected by Jaci when the class is processed.
    private CommandOutput output;

    @Command(description = "Display 'Hello, World!'")
    public void helloWorld() {
        output.message("Hello, World!");
    }

    @Command(description = "Throws an exception.")
    public void exception() {
        throw new IllegalArgumentException("Exception thrown from within command!");
    }

    @Command(description = "Throws, catches and re-throws an exception.")
    public void exceptionRethrow() {
        try {
            exception();
        } catch (Exception e) {
            throw new RuntimeException("Rethrown exception", e);
        }
    }

    @Command(description = "1st int param is mandatory, 2nd String param is optional, 3rd Boolean param is optional")
    public void paramExample(@IntParam("mandatoryInt") int intParam,
                             @StringParam(value = "optionalString", optional = true, defaultValueSupplier = "defaultSupplier") String stringParam,
                             @BoolParam(value = "flag", optional = true, defaultValue = false) boolean flag) {
        output.message("mandatoryInt="+intParam+", optionalString="+stringParam+", flag=" + flag);
    }

    // If the value of "optionalString" isn't bound from the command line when calling the command 'paramExample' above,
    // this method will be invoked to supply a default value.
    private String defaultSupplier() {
        // Should be replaced by a real calculation
        return "suppliedDefault";
    }

    // Toggle commands are special commands that take a single optional boolean parameter
    // and toggle the state of some component on or off.
    @ToggleCommand(description = "Toggles something")
    public ToggleCommandStateAccessor toggle2() {
        return new ToggleCommandStateAccessor() {
            // Under real use we would want to toggle the state of some external component and not an internal boolean.
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

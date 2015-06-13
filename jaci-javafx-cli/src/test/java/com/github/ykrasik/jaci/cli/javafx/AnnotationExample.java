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

package com.github.ykrasik.jaci.cli.javafx;

import com.github.ykrasik.jaci.api.*;

/**
 * @author Yevgeny Krasik
 */
@CommandPath("annotation/example")
public class AnnotationExample {

    // TODO: More examples.
    private CommandOutput output;

    @CommandPath("new/path")
    @Command(description = "Does nothing, really.")
    public void testCommand(@StringParam(value = "str", acceptsSupplier = "testSupplier") String str,
                            @BoolParam("bool") boolean bool,
                            int integer) {
        output.message("str=%s, bool=%s, integer=%d", str, bool, integer);
    }

    @Command
    public void command() {
    }

    @Command(description = "Parameters example")
    public void paramExample(@IntParam(value = "mandatoryInt", description = "Mandatory int param") int intParam,
                             @StringParam(value = "optionalString", description = "Optional string param", optional = true, defaultValue = "default") String stringParam,
                             @BoolParam(value = "flag", optional = true) boolean flag) {
        output.message("mandatoryInt=%d, optionalString=%s, flag=%s", intParam, stringParam, flag);
    }

    private String[] testSupplier() {
        // This should execute some sort of computation... for the sake of example, returns a const array.
        return new String[] { "a", "b", "c", "d", "e", "ee" };
    }

    @ToggleCommand(description = "toggles this and that")
    public ToggleCommandStateAccessor toggleCommand() {
        return new ToggleCommandStateAccessor() {
            private boolean test;

            @Override
            public void set(boolean value) {
                test = value;
            }

            @Override
            public boolean get() {
                return test;
            }
        };
    }

    @Command(description = "some global command")
    public void someCommand(String str) {
        output.message("str=%s", str);
    }

    @Command(description = "test command")
    public void nestCommand(@StringParam(value = "nested", accepts = {"test1", "value2", "param3", "long string"}) String str,
                            @BoolParam("booleany") boolean booleany) {
        output.message("string = %s, booleany = %s", str, booleany);
    }

    @Command
    public void noParamAnnotations(int intParam, boolean boolParam, String stringParam, double doubleParam) {
        output.message("intParam=%d, boolParam=%s, stringParam=%s, doubleParam=%s", intParam, boolParam, stringParam, doubleParam);
    }
}

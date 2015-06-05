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

package com.github.ykrasik.jemi.libgdx;

import com.github.ykrasik.jemi.api.*;

/**
 * @author Yevgeny Krasik
 */
@CommandPath("annotation/example")
public class AnnotationExample {

    @CommandPath("new/path")
    @Command(description = "Does nothing, really.")
    public void testCommand(CommandOutput output,
                            @StringParam(value = "str", optional = true, defaultValue = "lala") String str,
                            @BoolParam("bool") boolean bool,
                            int integer) {
        output.message("Oh yeah, str=%s, bool=%s, integer=%d", str, bool, integer);
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
    public void globalCommandSomething(CommandOutput output, String str) {
        output.message("yes! str=%s", str);
    }

    @Command(description = "test command")
    public void nestCommand(CommandOutput output,
                            @StringParam(value = "nested", accepts = {"test1", "value2", "param3", "long string"}) String str,
                            @BoolParam("booleany") boolean booleany) {
        output.message("yay: string = %s, booleany = %s", str, booleany);
    }

    @Command
    public void test(CommandOutput output,
                     @StringParam(supplier = "supplier") String str,
                     @BoolParam(optional = true, defaultValueSupplier = "boolSupplier1") boolean bool1,
                     @BoolParam(optional = true, defaultValueSupplier = "boolSupplier2") Boolean bool2) {
        output.message("str=%s, bool1=%s, bool2=%s", str, bool1, bool2);
    }

    private String[] supplier() {
        return new String[]{ "a", "b", "c" };
    }

    private Boolean boolSupplier1() {
        return true;
    }

    private boolean boolSupplier2() {
        return false;
    }
}

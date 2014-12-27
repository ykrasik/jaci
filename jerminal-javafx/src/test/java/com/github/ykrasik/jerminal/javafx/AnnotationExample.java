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

package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.annotation.*;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.toggle.StateAccessor;

/**
 * @author Yevgeny Krasik
 */
@ShellPath("annotation/example")
public class AnnotationExample {

    // TODO: More examples.

    @ShellPath("new/path")
    @Command(description = "Does nothing, really.")
    public void testCommand(OutputPrinter outputPrinter,
                            @DynamicStringParam(value = "str", supplier = "testSupplier") String str,
                            @BoolParam("bool") boolean bool,
                            int integer) {
        outputPrinter.println("Oh yeah, str=%s, bool=%s, integer=%d", str, bool, integer);
    }

    @Command(description = "Parameters example")
    public void paramExample(OutputPrinter outputPrinter,
                             @IntParam(value = "mandatoryInt", description = "Mandatory int param") int intParam,
                             @StringParam(value = "optionalString", description = "Optional string param", optional = true, defaultValue = "default") String stringParam,
                             @FlagParam("flagParam") boolean flag) {
        outputPrinter.println("mandatoryInt=%d", intParam);
        outputPrinter.println("optionalString=%s", stringParam);
        outputPrinter.println("flagParam=%s", flag);
    }

    private String[] testSupplier() {
        // This should execute some sort of computation... for the sake of example, returns a const array.
        return new String[] { "a", "b", "c", "d", "e", "ee" };
    }

    @ToggleCommand(description = "toggles this and that")
    public StateAccessor toggleCommand() {
        return new StateAccessor() {
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

    @ShellPath(global = true)
    @Command(description = "some global command")
    public void globalCommandSomething(OutputPrinter outputPrinter, String str) {
        outputPrinter.println("yes! str=%s", str);
    }

    @CommandFactory
    public com.github.ykrasik.jerminal.api.filesystem.command.Command commandFromFactory() {
        return new CommandBuilder("bla")
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws Exception {
                    outputPrinter.println("Great success.");
                }
            })
            .build();
    }

    @Command(description = "test command")
    public void nestCommand(OutputPrinter outputPrinter,
                            @StringParam(value = "nested", accepts = {"test1", "value2", "param3", "long string"}) String str,
                            @BoolParam("booleany") boolean booleany) {
        outputPrinter.println("yay: string = %s, booleany = %s", str, booleany);
    }
}

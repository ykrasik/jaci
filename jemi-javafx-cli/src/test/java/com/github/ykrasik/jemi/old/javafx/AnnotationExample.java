///******************************************************************************
// * Copyright (C) 2014 Yevgeny Krasik                                          *
// *                                                                            *
// * Licensed under the Apache License, Version 2.0 (the "License");            *
// * you may not use this file except in compliance with the License.           *
// * You may obtain a copy of the License at                                    *
// *                                                                            *
// * http://www.apache.org/licenses/LICENSE-2.0                                 *
// *                                                                            *
// * Unless required by applicable law or agreed to in writing, software        *
// * distributed under the License is distributed on an "AS IS" BASIS,          *
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
// * See the License for the specific language governing permissions and        *
// * limitations under the License.                                             *
// ******************************************************************************/
//
//package com.github.ykrasik.jemi.old.javafx;
//
//import com.github.ykrasik.jemi.api.CommandOutput;
//import com.github.ykrasik.jemi.api.annotation.*;
//import com.github.ykrasik.jerminal.old.command.toggle.StateAccessor;
//
///**
// * @author Yevgeny Krasik
// */
//@ShellPath("annotation/example")
//public class AnnotationExample {
//
//    // TODO: More examples.
//    private CommandOutput commandOutput;
//
//    @ShellPath("new/path")
//    @Command(description = "Does nothing, really.")
//    public void testCommand(CommandOutput commandOutput,
//                            @DynamicStringParam(value = "str", supplier = "testSupplier") String str,
//                            @BoolParam("bool") boolean bool,
//                            int integer) {
//        commandOutput.message("Oh yeah, str=%s, bool=%s, integer=%d", str, bool, integer);
//    }
//
//    @Command
//    public void commandWithoutPrinter() {
//        System.out.println("asd");
//    }
//
//    @Command(description = "Parameters example")
//    public void paramExample(CommandOutput commandOutput,
//                             @IntParam(value = "mandatoryInt", description = "Mandatory int param") int intParam,
//                             @StringParam(value = "optionalString", description = "Optional string param", optional = true, defaultValue = "default") String stringParam,
//                             @FlagParam("flagParam") boolean flag) {
//        commandOutput.message("mandatoryInt=%d", intParam);
//        commandOutput.message("optionalString=%s", stringParam);
//        commandOutput.message("flagParam=%s", flag);
//    }
//
//    private String[] testSupplier() {
//        // This should execute some sort of computation... for the sake of example, returns a const array.
//        return new String[] { "a", "b", "c", "d", "e", "ee" };
//    }
//
//    @ToggleCommand(description = "toggles this and that")
//    public StateAccessor toggleCommand() {
//        return new StateAccessor() {
//            private boolean test;
//
//            @Override
//            public void set(boolean value) {
//                test = value;
//            }
//
//            @Override
//            public boolean get() {
//                return test;
//            }
//        };
//    }
//
//    @ShellPath(global = true)
//    @Command(description = "some global command")
//    public void globalCommandSomething(CommandOutput commandOutput, String str) {
//        commandOutput.message("yes! str=%s", str);
//    }
//
//    @Command(description = "test command")
//    public void nestCommand(CommandOutput commandOutput,
//                            @StringParam(value = "nested", accepts = {"test1", "value2", "param3", "long string"}) String str,
//                            @BoolParam("booleany") boolean booleany) {
//        commandOutput.message("yay: string = %s, booleany = %s", str, booleany);
//    }
//
//    @Command
//    public void noParamAnnotations(CommandOutput commandOutput, int intParam, boolean boolParam, String stringParam, double doubleParam) {
//        commandOutput.message("intParam=%d, boolParam=%s, stringParam=%s, doubleParam=%s", intParam, boolParam, stringParam, doubleParam);
//    }
//}

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

package com.github.ykrasik.jerminal.core.annotation.param;

import com.github.ykrasik.jerminal.api.*;
import com.github.ykrasik.jerminal.api.CommandOutput;
import com.github.ykrasik.jerminal.api.ToggleCommandStateAccessor;

import static org.mockito.Mockito.mock;

/**
 * @author Yevgeny Krasik
 */
public class AnnotationCommandParamFactoryTest {
    // FIXME: Implement

    private static class AnnotationParamTestClass {
        public boolean noOutputPrinterNoArgsExecuted;
        public boolean noArgsExecuted;
        public boolean customNameExecuted;
        public boolean customDescriptionExecuted;
        public boolean boolParamExecuted;
        public boolean boolParamNoOutputPrinterExecuted;
        public boolean doubleParamIntParamExecuted;
        public boolean doubleParamIntParamNoOutputPrinterExecuted;
        public boolean flagParamStaticStringParamDynamicStringParamExecuted;
        public boolean flagParamStaticStringParamDynamicStringParamNoOutputPrinterExecuted;
        public boolean allParamTypesExecuted;
        public boolean allParamTypesNoOutputPrinterExecuted;
        public boolean toggleCommandExecuted;

        @Command
        public void noOutputPrinterNoArgs() {
            noOutputPrinterNoArgsExecuted = true;
        }

        @Command
        public void noArgs(CommandOutput commandOutput) {
            noArgsExecuted = true;
        }

        @Command("customName")
        public void commandCustomName() {
            customNameExecuted = true;
        }

        @Command(description = "custom description")
        public void customDescription() {
            customDescriptionExecuted = true;
        }

        @Command
        public void boolParam(CommandOutput commandOutput, @BoolParam boolean bool) {
            boolParamExecuted = true;
        }

        @Command
        public void boolParamNoOutputPrinter(@BoolParam Boolean bool) {
            boolParamNoOutputPrinterExecuted = true;
        }

        @Command
        public void doubleParamIntParam(CommandOutput commandOutput, @DoubleParam double doubleParam, @IntParam int intParam) {
            doubleParamIntParamExecuted = true;
        }

        @Command
        public void doubleParamIntParamNoOutputPrinter(@DoubleParam Double doubleParam, @IntParam Integer intParam) {
            doubleParamIntParamNoOutputPrinterExecuted = true;
        }

        @Command
        public void flagParamStaticStringParamDynamicStringParam(CommandOutput commandOutput,
                                                                 @FlagParam boolean flag,
                                                                 @StringParam String staticString,
                                                                 @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
            flagParamStaticStringParamDynamicStringParamExecuted = true;
        }

        @Command
        public void flagParamStaticStringParamDynamicStringParamNoOutputPrinter(@FlagParam Boolean flag,
                                                                                @StringParam String staticString,
                                                                                @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
            flagParamStaticStringParamDynamicStringParamNoOutputPrinterExecuted = true;
        }

        @Command
        public void allParamTypes(CommandOutput commandOutput,
                                  @BoolParam boolean bool,
                                  @DoubleParam double doubleParam,
                                  @IntParam int intParam,
                                  @FlagParam boolean flag,
                                  @StringParam String staticString,
                                  @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
            allParamTypesExecuted = true;
        }

        @Command
        public void allParamTypesNoOutputPrinter(@BoolParam Boolean bool,
                                                 @DoubleParam Double doubleParam,
                                                 @IntParam Integer intParam,
                                                 @FlagParam Boolean flag,
                                                 @StringParam String staticString,
                                                 @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
            allParamTypesNoOutputPrinterExecuted = true;
        }

        @ToggleCommand
        public ToggleCommandStateAccessor toggleCommand() {
            return new ToggleCommandStateAccessor() {
                @Override
                public void set(boolean value) {
                    toggleCommandExecuted = true;
                }

                @Override
                public boolean get() {
                    return false;
                }
            };
        }

        @ToggleCommand
        public void invalidToggleCommand() {

        }

        @ToggleCommand
        public ToggleCommandStateAccessor invalidToggleCommand2(int invalid) {
            return mock(ToggleCommandStateAccessor.class);
        }

        @Command
        public void invalidBoolParam(@BoolParam int invalid) {

        }

        @Command
        public void invalidDoubleParam(@DoubleParam int invalid) {

        }

        @Command
        public void invalidIntParam(@IntParam String invalid) {

        }

        @Command
        public void invalidStringParam(@StringParam int invalid) {

        }

        @Command
        public void invalidDynamicStringParam(@DynamicStringParam(supplier = "dummySupplier") int invalid) {

        }

        @Command
        public void invalidParamType(Object invalid) {

        }
    }
}

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

import com.github.ykrasik.jaci.api.Command;
import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.api.CommandPath;

/**
 * Examples of class nesting.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("innerClass")
public class InnerClassCommands {
    private CommandOutput output;

    @CommandPath("inner1")
    public class Inner1 {
        @Command
        public void test() {
            output.message("Inner1: Should be under /innerClass/inner1/");
        }

        @CommandPath("inner2")
        public class Inner2 {
            @Command
            public void test() {
                output.message("Inner2: Should be under /innerClass/inner1/inner2/");
            }

            @CommandPath("inner3")
            public class Inner3 {
                // This one has it's own output defined, even though it will end up being the same instance.
                private CommandOutput output;

                @Command
                public void test() {
                    output.message("Inner3: Should be under /innerClass/inner1/inner2/inner3/");
                }
            }
        }

        // Inner class without annotation - commands will receive outer class's path.
        public class Inner2WithoutAnnotation {
            @Command
            public void test2() {
                output.message("Inner2WithoutAnnotation: Should be under /innerClass/inner1/");
            }
        }
    }

    // Inner class without annotation - commands will receive outer class's path.
    public class InnerWithoutAnnotation {
        @Command
        public void test() {
            output.message("InnerWithoutAnnotation: Should be under /innerClass/");
        }
    }
}

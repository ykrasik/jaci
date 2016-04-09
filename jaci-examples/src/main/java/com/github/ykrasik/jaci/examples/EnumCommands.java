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
import com.github.ykrasik.jaci.api.EnumParam;

/**
 * Examples with enum parameters.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("enum")
public class EnumCommands {
    private CommandOutput output;

    @Command
    public void paramExample(@EnumParam("first") Enum1 first,
                             @EnumParam(value = "second", optional = true, defaultValue = "Dummy") Enum1 second,
                             @EnumParam(value = "third", optional = true, defaultValueSupplier = "supply") Enum2 third,
                             Enum2 fourth) {
        output.message("first="+first+", second="+second+", third=" + third + ", fourth=" + fourth);
    }

    private Enum2 supply() {
        return Enum2.var;
    }

    public enum Enum1 {
        value, ANOTHER_VALUE, CASEcheck, Dummy
    }

    public enum Enum2 {
        val, var
    }
}

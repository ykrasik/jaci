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

import com.github.ykrasik.jaci.api.Command;
import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.api.CommandPath;
import com.github.ykrasik.jaci.api.StringParam;

/**
 * A string param may be constrained to only accept a certain set of values.
 * These are either pre-determined (set in the annotation) or computed at runtime via a supplier.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("stringParam")
public class StringParamCommands {
    private CommandOutput output;

    @Command(description = "String param that accepts all values")
    public void unconstrainedString(@StringParam(accepts = {}) String str) {
        output.message("unconstrainedString: str=" + str);
    }

    @Command(description = "String param that only accepts values stated in the annotation.")
    public void staticConstrainedString(@StringParam(accepts = {"a", "b", "c"}) String str) {
        output.message("staticConstrainedString: str=" + str);
    }

    @Command(description = "String param that only accepts values supplied at runtime via a supplier.")
    public void dynamicConstrainedString(@StringParam(acceptsSupplier = "stringSupplier") String str) {
        output.message("dynamicConstrainedString: str=" + str);
    }

    private String[] stringSupplier() {
        return new String[]{ "d", "e", "f" };
    }
}

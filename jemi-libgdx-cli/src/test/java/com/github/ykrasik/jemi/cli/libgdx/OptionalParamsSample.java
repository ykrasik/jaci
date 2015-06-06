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

package com.github.ykrasik.jemi.cli.libgdx;

import com.github.ykrasik.jemi.api.*;

/**
 * These are all the ways of declaring commands with optional params.
 * Optional params may either have a const default value (set in the annotation) or a default value that is supplied
 * at runtime via a supplier.
 * Primitives and their boxed version are interchangeable.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("optionalParams")
public class OptionalParamsSample {
    @Command(description = "All params are optional, with const default values.")
    public void defaultConstValues(CommandOutput output,
                                   @BoolParam(value = "b", optional = true, defaultValue = true) boolean b,
                                   @IntParam(value = "i", optional = true, defaultValue = 5) int i,
                                   @DoubleParam(value = "d", optional = true, defaultValue = 4.5) double d,
                                   @StringParam(value = "str", optional = true, defaultValue = "default") String str) {
        output.message("defaultConstValues: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    @Command(description = "All params are optional, with default value suppliers.")
    public void defaultValueSuppliers(CommandOutput output,
                                      @BoolParam(value = "b", optional = true, defaultValueSupplier = "booleanSupplier") boolean b,
                                      @IntParam(value = "i", optional = true, defaultValueSupplier = "intSupplier") int i,
                                      @DoubleParam(value = "d", optional = true, defaultValueSupplier = "doubleSupplier") double d,
                                      @StringParam(value = "str", optional = true, defaultValueSupplier = "stringSupplier") String str) {
        output.message("defaultValueSuppliers: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    private Boolean booleanSupplier() {
        return true;
    }

    private int intSupplier() {
        return 2;
    }

    private double doubleSupplier() {
        return 3.5;
    }

    private String stringSupplier() {
        return "suppliedDefault";
    }
}

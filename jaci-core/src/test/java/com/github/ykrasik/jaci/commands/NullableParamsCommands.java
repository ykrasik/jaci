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

package com.github.ykrasik.jaci.commands;

import com.github.ykrasik.jaci.api.*;

/**
 * Examples of nullable parameters.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("nullable")
public class NullableParamsCommands {
    private CommandOutput output;

    @Command(description = "All non-primitive params are nullable by default")
    public void nullable(@IntParam("nullableInt") Integer intParam,
                         @StringParam("nullableString") String stringParam,
                         @BoolParam("nullableBool") Boolean boolParam,
                         @DoubleParam("nullableDouble") Double doubleParam,
                         @EnumParam("nullableEnum") TestEnum enumParam) {
        output.message("nullableInt="+intParam+", nullableString="+stringParam+", nullableBool="+boolParam+", nullableDouble="+doubleParam+", nullableEnum="+enumParam);
    }

    @Command(description = "Nullable notation ignored for primitives")
    public void primitives(@IntParam(value = "primitiveInt", nullable = true) int intParam,
                           @BoolParam(value = "primitiveBool", nullable = true) boolean boolParam,
                           @DoubleParam(value = "primitiveDouble", nullable = true) double doubleParam) {
        output.message("primitiveInt="+intParam+", primitiveBool="+boolParam+", primitiveDouble="+doubleParam);
    }

    @Command(description = "Non-primitives that aren't nullable")
    public void nonNullable(@IntParam(value = "nonNullableInt", nullable = false) Integer intParam,
                            @StringParam(value = "nonNullableString", nullable = false) String stringParam,
                            @BoolParam(value = "nonNullableBool", nullable = false) Boolean boolParam,
                            @DoubleParam(value = "nonNullableDouble", nullable = false) Double doubleParam,
                            @EnumParam(value = "nonNullableEnum", nullable = false) TestEnum enumParam) {
        output.message("nonNullableInt="+intParam+", nonNullableString="+stringParam+", nonNullableBool="+boolParam+", nonNullableDouble="+doubleParam+", nonNullableEnum="+enumParam);
    }

    @Command(description = "Nullable & optional")
    public void optionalNullable(@IntParam(value = "optionalInt", optional = true) Integer intParam,
                                 @StringParam(value = "optionalString", optional = true) String stringParam,
                                 @BoolParam(value = "optionalBool", optional = true) Boolean boolParam,
                                 @DoubleParam(value = "optionalDouble", optional = true) Double doubleParam,
                                 @EnumParam(value = "optionalEnum", optional = true, defaultValue = "VALUE") TestEnum enumParam) {
        output.message("optionalInt="+intParam+", optionalString="+stringParam+", optionalBool="+boolParam+", optionalDouble="+doubleParam+", optionalEnum="+enumParam);
    }

    private enum TestEnum {
        VALUE
    }
}

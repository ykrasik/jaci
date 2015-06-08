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

import com.github.ykrasik.jaci.api.*;

/**
 * These are all the ways of declaring commands with mandatory params.
 * Primitives and their boxed version are interchangeable.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("mandatoryParams")
public class MandatoryParamsSample {
    @Command(description = "Primitive params without annotations. Without annotations all parameters are considered mandatory.")
    public void primitiveParamsNoAnnotations(CommandOutput output, boolean b, int i, double d, String str) {
        output.message("primitiveParamsNoAnnotations: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    @Command(description = "Boxed (nullable primitive) params without annotations. Without annotations all parameters are considered mandatory.")
    public void boxedParamsNoAnnotations(CommandOutput output, Boolean b, Integer i, Double d, String str) {
        output.message("boxedParamsNoAnnotations: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    @Command(description = "Primitive params with annotations, but annotations have no data.")
    public void primitiveParamsWithDefaultAnnotations(CommandOutput output,
                                                      @BoolParam boolean b,
                                                      @IntParam int i,
                                                      @DoubleParam double d,
                                                      @StringParam String str) {
        output.message("primitiveParamsWithDefaultAnnotations: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    @Command(description = "Primitive params with annotations")
    public void primitiveParamsWithAnnotations(CommandOutput output,
                                               @BoolParam(value = "b", description = "Mandatory bool") boolean b,
                                               @IntParam(value = "i", description = "Mandatory int") int i,
                                               @DoubleParam(value = "d", description = "Mandatory double") double d,
                                               @StringParam(value = "str", description = "Mandatory String") String str) {
        output.message("primitiveParamsWithAnnotations: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    @Command(description = "Boxed (nullable primitive) with annotations, but annotations have no data.")
    public void boxedParamsWithDefaultAnnotations(CommandOutput output,
                                                  @BoolParam Boolean b,
                                                  @IntParam Integer i,
                                                  @DoubleParam Double d,
                                                  @StringParam String str) {
        output.message("boxedParamsWithDefaultAnnotations: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }

    @Command(description = "Boxed (nullable primitive) with annotations")
    public void boxedParamsWithAnnotations(CommandOutput output,
                                           @BoolParam(value = "b", description = "Mandatory Boolean") Boolean b,
                                           @IntParam(value = "i", description = "Mandatory Integer") Integer i,
                                           @DoubleParam(value = "d", description = "Mandatory Double") Double d,
                                           @StringParam(value = "str", description = "Mandatory String") String str) {
        output.message("boxedParamsWithAnnotations: b=%s, i=%s, d=%s, str=%s", b, i, d, str);
    }
}

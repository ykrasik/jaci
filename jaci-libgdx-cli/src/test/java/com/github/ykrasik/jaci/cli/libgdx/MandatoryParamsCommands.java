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
public class MandatoryParamsCommands {
    private CommandOutput output;

    @Command(description = "Primitive params without annotations. Without annotations all parameters are considered mandatory.")
    public void primitiveParamsNoAnnotations(boolean b, int i, double d, String str) {
        output.message("primitiveParamsNoAnnotations: b="+b+", i="+i+", d="+d+", str="+str);
    }

    @Command(description = "Boxed (nullable primitive) params without annotations. Without annotations all parameters are considered mandatory.")
    public void boxedParamsNoAnnotations(Boolean b, Integer i, Double d, String str) {
        output.message("boxedParamsNoAnnotations: b="+b+", i="+i+", d="+d+", str="+str);
    }

    @Command(description = "Primitive params with annotations, but annotations have no data.")
    public void primitiveParamsWithDefaultAnnotations(@BoolParam boolean b,
                                                      @IntParam int i,
                                                      @DoubleParam double d,
                                                      @StringParam String str) {
        output.message("primitiveParamsWithDefaultAnnotations: b="+b+", i="+i+", d="+d+", str="+str);
    }

    @Command(description = "Primitive params with annotations")
    public void primitiveParamsWithAnnotations(@BoolParam(value = "b", description = "Mandatory bool") boolean b,
                                               @IntParam(value = "i", description = "Mandatory int") int i,
                                               @DoubleParam(value = "d", description = "Mandatory double") double d,
                                               @StringParam(value = "str", description = "Mandatory String") String str) {
        output.message("primitiveParamsWithAnnotations: b="+b+", i="+i+", d="+d+", str="+str);
    }

    @Command(description = "Boxed (nullable primitive) with annotations, but annotations have no data.")
    public void boxedParamsWithDefaultAnnotations(@BoolParam Boolean b,
                                                  @IntParam Integer i,
                                                  @DoubleParam Double d,
                                                  @StringParam String str) {
        output.message("boxedParamsWithDefaultAnnotations: b="+b+", i="+i+", d="+d+", str="+str);
    }

    @Command(description = "Boxed (nullable primitive) with annotations")
    public void boxedParamsWithAnnotations(@BoolParam(value = "b", description = "Mandatory Boolean") Boolean b,
                                           @IntParam(value = "i", description = "Mandatory Integer") Integer i,
                                           @DoubleParam(value = "d", description = "Mandatory Double") Double d,
                                           @StringParam(value = "str", description = "Mandatory String") String str) {
        output.message("boxedParamsWithAnnotations: b="+b+", i="+i+", d="+d+", str="+str);
    }
}

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

package com.github.ykrasik.jaci.cli.param;

import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.param.IntParamDef;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;
import lombok.NonNull;

/**
 * A {@link CliParam} that parses int values.
 * Cannot be auto-completed.
 *
 * @author Yevgeny Krasik
 */
public class IntCliParam extends AbstractNumericCliParam<Integer> {
    public IntCliParam(Identifier identifier, Opt<Spplr<Integer>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    protected String getValueTypeName() {
        return "int";
    }

    @Override
    protected Integer parseNumber(String arg) {
        return Integer.parseInt(arg);
    }

    /**
     * Construct a CLI int parameter from an {@link IntParamDef}.
     *
     * @param def IntParamDef to construct a CLI int parameter from.
     * @return A CLI int parameter constructed from the IntParamDef.
     */
    public static IntCliParam fromDef(@NonNull IntParamDef def) {
        return new IntCliParam(def.getIdentifier(), def.getDefaultValueSupplier());
    }
}

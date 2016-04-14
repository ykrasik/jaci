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
import com.github.ykrasik.jaci.param.DoubleParamDef;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

/**
 * A {@link CliParam} that parses double values.
 * Cannot be auto-completed.
 *
 * @author Yevgeny Krasik
 */
public class DoubleCliParam extends AbstractNumericCliParam<Double> {
    public DoubleCliParam(Identifier identifier, Opt<Spplr<Double>> defaultValueSupplier, boolean nullable) {
        super(identifier, defaultValueSupplier, nullable);
    }

    @Override
    protected String getValueTypeName() {
        return "double";
    }

    @Override
    protected Double parseNumber(String arg) {
        return Double.parseDouble(arg);
    }

    /**
     * Construct a CLI double parameter from a {@link DoubleParamDef}.
     *
     * @param def DoubleParamDef to construct a CLI double parameter from.
     * @return A CLI double parameter constructed from the DoubleParamDef.
     */
    public static DoubleCliParam fromDef(DoubleParamDef def) {
        return new DoubleCliParam(def.getIdentifier(), def.getDefaultValueSupplier(), def.isNullable());
    }
}

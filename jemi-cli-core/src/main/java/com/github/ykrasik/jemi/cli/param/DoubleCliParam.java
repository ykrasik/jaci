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

package com.github.ykrasik.jemi.cli.param;

import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.core.param.DoubleParamDef;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;

/**
 * A {@link CliParam} that parses double values.<br>
 * Cannot be auto-completed.
 *
 * @author Yevgeny Krasik
 */
public class DoubleCliParam extends AbstractNumericCliParam<Double> {
    public DoubleCliParam(Identifier identifier, Opt<Supplier<Double>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    protected String getParamTypeName() {
        return "double";
    }

    @Override
    protected Double parseNumber(String rawValue) {
        return Double.parseDouble(rawValue);
    }

    // TODO: JavaDoc
    public static DoubleCliParam fromDef(@NonNull DoubleParamDef def) {
        return new DoubleCliParam(def.getIdentifier(), def.getDefaultValueSupplier());
    }
}

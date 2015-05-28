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
import com.github.ykrasik.jemi.core.param.IntParamDef;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;

/**
 * A {@link CliParam} that parses int values.<br>
 * Cannot be auto-completed.
 *
 * @author Yevgeny Krasik
 */
public class IntCliParam extends AbstractNumericCliParam<Integer> {
    public IntCliParam(Identifier identifier, Opt<Supplier<Integer>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    protected String getParamTypeName() {
        return "int";
    }

    @Override
    protected Integer parseNumber(String rawValue) {
        return Integer.parseInt(rawValue);
    }

    // TODO: JavaDoc
    public static IntCliParam fromDef(@NonNull IntParamDef def) {
        return new IntCliParam(def.getIdentifier(), def.getDefaultValueSupplier());
    }
}

/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.command.parameter.numeric;

/**
 * A {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam} that parses double values.
 *
 * @author Yevgeny Krasik
 */
public class DoubleParam extends AbstractNumericCommandParam<Double> {
    public DoubleParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "double";
    }

    @Override
    protected Double parseNumber(String rawValue) {
        return Double.parseDouble(rawValue);
    }
}

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

import com.github.ykrasik.jaci.param.*;

/**
 * A {@link ParamDefResolver} that translates ParamDefs into CLI parameters.
 *
 * @author Yevgeny Krasik
 */
public class CliParamResolver implements ParamDefResolver<CliParam> {
    @Override
    public StringCliParam stringParam(StringParamDef def) {
        return StringCliParam.fromDef(def);
    }

    @Override
    public BooleanCliParam booleanParam(BooleanParamDef def) {
        return BooleanCliParam.fromDef(def);
    }

    @Override
    public IntCliParam intParam(IntParamDef def) {
        return IntCliParam.fromDef(def);
    }

    @Override
    public DoubleCliParam doubleParam(DoubleParamDef def) {
        return DoubleCliParam.fromDef(def);
    }

    @Override
    public <E extends Enum<E>> CliParam enumParam(EnumParamDef<E> def) {
        return EnumCliParam.fromDef(def);
    }
}

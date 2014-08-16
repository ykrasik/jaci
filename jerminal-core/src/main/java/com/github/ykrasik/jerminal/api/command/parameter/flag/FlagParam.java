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
package com.github.ykrasik.jerminal.api.command.parameter.flag;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamType;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.exception.ParseError;

/**
 * A special boolean {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam} that does not parse values.<br>
 * A {@link FlagParam}'s value is set by it's presence - If it is present in the command line, it is set to true, Otherwise, it is false.<br>
 * {@link FlagParam}s cannot be passed "by name". For example, if we have a {@link FlagParam} called "-r",
 * the following is illegal: "... -r=true ...". The correct way to do it is "... -r ...".
 *
 * @author Yevgeny Krasik
 */
public class FlagParam extends AbstractDescribable implements CommandParam {
    public FlagParam(String name, String description) {
        super(name, description);
    }

    @Override
    public ParamType getType() {
        return ParamType.FLAG;
    }

    @Override
    public String getExternalForm() {
        return String.format("[%s: flag]", getName());
    }

    @Override
    public Boolean parse(String rawValue) throws ParseException {
        throw invalidFlagValue();
    }

    @Override
    public Boolean unbound() throws ParseException {
        return false;
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        throw invalidFlagValue();
    }

    @Override
    public String toString() {
        return getExternalForm();
    }

    private ParseException invalidFlagValue() {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Flag parameters take no value: '%s'", getName()
        );
    }
}

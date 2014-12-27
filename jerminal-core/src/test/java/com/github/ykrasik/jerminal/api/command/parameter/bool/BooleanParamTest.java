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

package com.github.ykrasik.jerminal.api.command.parameter.bool;

import com.github.ykrasik.jerminal.api.command.parameter.BaseParamTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Yevgeny Krasik
 */
public class BooleanParamTest extends BaseParamTest<Boolean> {
    @Before
    public void setUp() {
        param = new BooleanParam("boolean", "boolean");
    }

    @Test
    public void parseTest() {
        // Boolean params should only accept 'true' and 'false'
        parse("true", true);
        parse("True", true);
        parse("TRUE", true);

        parse("false", false);
        parse("False", false);
        parse("FALSE", false);

        parseInvalid("true1");
        parseInvalid("false1");
        parseInvalid("tru");
        parseInvalid("fals");
        parseInvalid("t");
        parseInvalid("f");
        parseInvalid("1");
        parseInvalid("0");
        parseInvalid("");
    }

    @Test
    public void autoCompleteTest() {
        autoComplete("", "true", "false");

        autoComplete("t", "true");
        autoComplete("T", "true");
        autoComplete("tr", "true");
        autoComplete("Tru", "true");
        autoComplete("True", "true");
        autoComplete("TRUE", "true");

        autoComplete("f", "false");
        autoComplete("F", "false");
        autoComplete("fa", "false");
        autoComplete("Fal", "false");
        autoComplete("fals", "false");
        autoComplete("false", "false");
        autoComplete("FALSE", "false");

        autoCompleteEmpty("a");
        autoCompleteEmpty("true1");
        autoCompleteEmpty("t1rue");
        autoCompleteEmpty("false1");
        autoCompleteEmpty("faalse");
    }
}

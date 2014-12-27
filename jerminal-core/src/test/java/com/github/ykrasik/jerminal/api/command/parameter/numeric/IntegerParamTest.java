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

import com.github.ykrasik.jerminal.api.command.parameter.BaseParamTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Yevgeny Krasik
 */
public class IntegerParamTest extends BaseParamTest<Integer> {
    @Before
    public void setUp() {
        param = new IntegerParamBuilder("integer").build();
    }

    @Test
    public void parseTest() {
        // Numeric params should only accept numeric values.
        parse("1", 1);
        parse("23", 23);
        parse("4567", 4567);
        parse("-123456", -123456);
        parse("999999", 999999);

        parseInvalid("a");
        parseInvalid("0a");
        parseInvalid("1a");
        parseInvalid("9999a");
        parseInvalid("100a11");
    }

    @Test
    public void autoCompleteTest() {
        // Numeric params cannot be auto completed.
        autoCompleteInvalid("15");
        autoCompleteInvalid("1");
        autoCompleteInvalid("5a");
    }
}

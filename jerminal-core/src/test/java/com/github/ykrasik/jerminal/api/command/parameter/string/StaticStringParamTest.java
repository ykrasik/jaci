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

package com.github.ykrasik.jerminal.api.command.parameter.string;

import com.github.ykrasik.jerminal.api.command.parameter.BaseParamTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Yevgeny Krasik
 */
public class StaticStringParamTest extends BaseParamTest<String> {
    private static final String[] VALUES = { "a", "bc", "1122", "abra", "cadabra" };

    @Before
    public void setUp() {
        param = new StringParamBuilder("string").setConstantAcceptableValues(VALUES).build();
    }

    @Test
    public void parseTest() {
        // Statically-constrained string params should only accept strings in their constraints.
        for (String value : VALUES) {
            parse(value, value);
        }

        parse("Abra", "Abra");
        parse("ABRA", "ABRA");
        parse("Cadabra", "Cadabra");
        parse("A", "A");

        parseInvalid("ab");
        parseInvalid("bd");
        parseInvalid("bcd");
        parseInvalid("112");
        parseInvalid("abra1");
        parseInvalid("1cadabra");
    }

    @Test
    public void autoCompleteTest() {
        // Statically-constrained string params should auto complete strings in their constraints.
        autoComplete("", VALUES);
        autoComplete("a", "a", "abra");
        autoComplete("A", "A", "Abra");
        autoComplete("b", "bc");
        autoComplete("bc", "bc");
        autoComplete("bC", "bC");
        autoComplete("1", "1122");
        autoComplete("112", "1122");
        autoComplete("ab", "abra");
        autoComplete("c", "cadabra");
        autoComplete("cad", "cadabra");
        autoComplete("cadA", "cadAbra");

        autoCompleteEmpty("cad1");
        autoCompleteEmpty("d");
    }
}

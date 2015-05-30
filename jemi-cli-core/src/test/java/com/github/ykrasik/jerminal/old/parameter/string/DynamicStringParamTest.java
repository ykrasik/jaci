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

package com.github.ykrasik.jerminal.old.parameter.string;

import com.github.ykrasik.jerminal.old.parameter.BaseParamTest;
import com.google.common.base.Supplier;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
public class DynamicStringParamTest extends BaseParamTest<String> {
    private List<String> values = Collections.emptyList();

    private Supplier<List<String>> supplier = new Supplier<List<String>>() {
        @Override
        public List<String> get() {
            return values;
        }
    };

    @Before
    public void setUp() {
        param = new StringParamBuilder("string").setDynamicAcceptableValuesSupplier(supplier).build();
    }

    @Test
    public void parseTest() {
        // Dynamically-constrained string params should only accept strings in their constraints.
        setValues("a", "bc", "1122", "abra", "cadabra", "e f g");
        assertParseValues();

        parse("Abra", "Abra");
        parse("ABRA", "ABRA");
        parse("Cadabra", "Cadabra");
        parse("A", "A");

        parseInvalid("");
        parseInvalid("ab");
        parseInvalid("bd");
        parseInvalid("bcd");
        parseInvalid("112");
        parseInvalid("abra1");
        parseInvalid("1cadabra");
        parseInvalid("e");
        parseInvalid("e f");
        parseInvalid("e f gh");
        parseInvalid("e f g h");

        setValues("a", "b", "c");
        assertParseValues();

        parse("a", "a");
        parse("A", "A");
        parse("b", "b");
        parse("B", "B");
        parse("c", "c");
        parse("C", "C");

        parseInvalid("d");
        parseInvalid("ab");
    }

    @Test
    public void autoCompleteTest() {
        // Statically-constrained string params should auto complete strings in their constraints.
        setValues("a", "bc", "1122", "abra", "cadabra", "e f g");
        autoComplete("", (String[]) values.toArray());
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
        autoComplete("e", "e f g");
        autoComplete("e f", "e f g");

        autoCompleteEmpty("cad1");
        autoCompleteEmpty("d");

        setValues("a", "b", "c");
        autoComplete("", "a", "b", "c");
        autoComplete("a", "a");
        autoComplete("A", "A");
        autoComplete("b", "b");
        autoComplete("B", "B");
        autoComplete("c", "c");
        autoComplete("C", "C");

        autoCompleteEmpty("ab");
        autoCompleteEmpty("d");
        autoCompleteEmpty("ef");
        autoCompleteEmpty("e f1");
    }

    private void setValues(String... values) {
        this.values = Arrays.asList(values);
    }

    private void assertParseValues() {
        for (String value : values) {
            parse(value, value);
        }
    }
}

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
import org.junit.Before;
import org.junit.Test;

/**
 * @author Yevgeny Krasik
 */
public class UnconstrainedStringParamTest extends BaseParamTest<String> {
    @Before
    public void setUp() {
        param = new StringParamBuilder("string").build();
    }

    @Test
    public void parseTest() {
        // Unconstrained string params should accept all strings.
        parse("test", "test");
        parse("a11bc23", "a11bc23");
        parse("", "");
        parse("a b c", "a b c");
    }

    @Test
    public void autoCompleteTest() {
        // Unconstrained string params cannot be auto completed.
        autoCompleteEmpty("");
        autoCompleteEmpty("test");
        autoCompleteEmpty("a11bc23");
        autoCompleteEmpty("a b c");
    }
}

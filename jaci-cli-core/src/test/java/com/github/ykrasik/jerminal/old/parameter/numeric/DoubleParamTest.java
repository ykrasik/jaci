///*
// * Copyright (C) 2014 Yevgeny Krasik
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.github.ykrasik.jerminal.old.parameter.numeric;
//
//import com.github.ykrasik.jerminal.old.parameter.BaseParamTest;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * @author Yevgeny Krasik
// */
//public class DoubleParamTest extends BaseParamTest<Double> {
//    @Before
//    public void setUp() {
//        param = new DoubleParamBuilder("double").build();
//    }
//
//    @Test
//    public void parseTest() {
//        // Numeric params should only accept numeric values.
//        parse("1", 1.0);
//        parse("1.", 1.0);
//        parse("1.5", 1.5);
//        parse("2.345", 2.345);
//        parse("-0.123456", -0.123456);
//        parse("99.9999", 99.9999);
//
//        parseInvalid("a");
//        parseInvalid("0.a");
//        parseInvalid("1.a");
//        parseInvalid("99.99a");
//        parseInvalid("100a11");
//    }
//
//    @Test
//    public void autoCompleteTest() {
//        // Numeric params cannot be auto completed.
//        autoCompleteInvalid("1.5");
//        autoCompleteInvalid("1.");
//        autoCompleteInvalid("5a");
//    }
//}

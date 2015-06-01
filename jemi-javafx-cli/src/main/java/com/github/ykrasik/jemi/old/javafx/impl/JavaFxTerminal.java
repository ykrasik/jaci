///******************************************************************************
// * Copyright (C) 2014 Yevgeny Krasik                                          *
// *                                                                            *
// * Licensed under the Apache License, Version 2.0 (the "License");            *
// * you may not use this file except in compliance with the License.           *
// * You may obtain a copy of the License at                                    *
// *                                                                            *
// * http://www.apache.org/licenses/LICENSE-2.0                                 *
// *                                                                            *
// * Unless required by applicable law or agreed to in writing, software        *
// * distributed under the License is distributed on an "AS IS" BASIS,          *
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
// * See the License for the specific language governing permissions and        *
// * limitations under the License.                                             *
// ******************************************************************************/
//
//package com.github.ykrasik.jemi.old.javafx.impl;
//
//import javafx.scene.control.TextArea;
//
//import java.util.Objects;
//
///**
// * A {@link Terminal} implemented as as a JavaFx {@link TextArea}.
// *
// * @author Yevgeny Krasik
// */
//public class JavaFxTerminal implements Terminal {
//    private final TextArea textArea;
//
//    public JavaFxTerminal(TextArea textArea) {
//        this.textArea = Objects.requireNonNull(textArea);
//    }
//
//    @Override
//    public String getTab() {
//        return "\t";
//    }
//
//    @Override
//    public void begin() {
//        // Nothing to do here.
//    }
//
//    @Override
//    public void end() {
//        // Nothing to do here.
//    }
//
//    @Override
//    public void println(String text, TerminalColor color) {
//        // Color printing is unsupported at this point.
//        textArea.appendText(text);
//        textArea.appendText("\n");
//    }
//}

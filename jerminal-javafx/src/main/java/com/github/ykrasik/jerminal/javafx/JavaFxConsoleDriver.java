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

package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.Console;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

/**
 * Links keyEvents to {@link Console} events.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxConsoleDriver implements EventHandler<KeyEvent> {
    private final Console console;

    public JavaFxConsoleDriver(Console console) {
        this.console = Objects.requireNonNull(console);
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
                console.execute();
                keyEvent.consume();
                break;

            case TAB:
                console.assist();
                keyEvent.consume();
                break;

            case UP:
                console.setPrevCommandLineFromHistory();
                keyEvent.consume();
                break;

            case DOWN:
                console.setNextCommandLineFromHistory();
                keyEvent.consume();
                break;
        }
    }
}

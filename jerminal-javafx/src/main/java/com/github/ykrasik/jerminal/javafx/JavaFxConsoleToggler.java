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

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

/**
 * An {@link EventHandler} that should be registered with a {@link javafx.scene.Scene} as a filter
 * {@link javafx.scene.Scene#addEventFilter(javafx.event.EventType, javafx.event.EventHandler)} for {@link KeyEvent#KEY_PRESSED}.
 * Will toggle the console on and off according to {@link KeyEvent}s.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxConsoleToggler implements EventHandler<KeyEvent> {
    private final Node console;

    public JavaFxConsoleToggler(Node console) {
        this.console = Objects.requireNonNull(console);
    }

    @Override
    public void handle(KeyEvent event) {
        if (shouldToggle(event)) {
            toggle();
        }
    }

    private void toggle() {
        final boolean toggle = !console.isVisible();
        console.setVisible(toggle);
    }

    /**
     * The default toggle combination is Ctrl+` (back tick, usually above tab).
     * Can be overridden by subclasses that want to toggle on a different combination.
     *
     * @param event KeyEvent to process.
     * @return True if the console should be toggled on this event.
     */
    protected boolean shouldToggle(KeyEvent event) {
        return event.isControlDown() && event.getCode() == KeyCode.BACK_QUOTE;
    }
}

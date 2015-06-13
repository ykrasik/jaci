/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.cli.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import lombok.NonNull;

/**
 * An {@link EventHandler} that toggles a node's visibility on or off when a key combination is pressed.
 * The default key combination is Ctrl+` (back tick, usually above tab).
 *
 * @author Yevgeny Krasik
 */
// FIXME: When doing a sceneToggle and the cursor is over the textArea, the cursor remains the text cursor.
public class NodeVisibilityToggler implements EventHandler<KeyEvent> {
    private static final KeyCombination DEFAULT_KEY_COMBINATION = KeyCombination.keyCombination("Ctrl+`");

    private final Node node;
    private KeyCombination toggleCombination = DEFAULT_KEY_COMBINATION;

    /**
     * Create a toggler that will toggle the given node when the default Ctrl+` (back-tick) key combination is detected.
     *
     * @param node Node whose visibility should be toggled when the key combination is detected.
     */
    public NodeVisibilityToggler(@NonNull Node node) {
        this.node = node;
    }

    /**
     * Set a new toggle keyCombination.
     *
     * @param toggleCombination New keyCombination to set.
     */
    public void setToggleCombination(@NonNull KeyCombination toggleCombination) {
        this.toggleCombination = toggleCombination;
    }

    /**
     * Register as an event filter with the given scene.
     * Will toggle on the configured key combination.
     *
     * @param scene Scene to register on.
     *
     * @see #setToggleCombination(KeyCombination)
     */
    public void register(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this);
    }

    @Override
    public void handle(KeyEvent event) {
        if (toggleCombination.match(event)) {
            node.setVisible(!node.isVisible());
        }
    }
}

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

package com.github.ykrasik.jemi.old.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

/**
 * An {@link javafx.event.EventHandler} that toggles a node's visibility on or off when a key combination is pressed.
 * The default key combination is Ctrl+` (back tick, usually above tab).
 *
 * @author Yevgeny Krasik
 */
// FIXME: BUG: When doing a sceneToggle and the cursor was over the textArea, the cursor remaings the text cursor.
public class NodeVisibilityToggler implements EventHandler<KeyEvent> {
    private static final KeyCombination DEFAULT_KEY_COMBINATION = KeyCombination.keyCombination("Ctrl+`");

    private final Node node;

    private KeyCombination toggleCombination = DEFAULT_KEY_COMBINATION;

    public NodeVisibilityToggler(Node node) {
        this.node = Objects.requireNonNull(node);
    }

    /**
     * Set a new toggle keyCombination.
     *
     * @param toggleCombination New keyCombination to set.
     */
    public void setToggleCombination(KeyCombination toggleCombination) {
        this.toggleCombination = Objects.requireNonNull(toggleCombination);
    }

    @Override
    public void handle(KeyEvent event) {
        if (toggleCombination.match(event)) {
            node.setVisible(!node.isVisible());
        }
    }

    /**
     * Register a {@link SceneToggler} as an event filter with the {@link javafx.scene.Scene}.
     * Will toggle on the default key combination of Ctrl+`
     *
     * @param scene Scene to register on.
     * @param parent The parent to show upon toggle.
     */
    public static void register(Scene scene, Parent parent) {
        register(scene, parent, DEFAULT_KEY_COMBINATION);
    }

    /**
     * Register a {@link SceneToggler} as an event filter with the {@link javafx.scene.Scene}.
     * Will toggle on the supplied key combination.
     *
     * @param scene Stage to register on.
     * @param parent The parent to show upon toggle.
     * @param toggleCombination The key combination to toggle on.
     */
    public static void register(Scene scene, Parent parent, KeyCombination toggleCombination) {
        final NodeVisibilityToggler toggler = new NodeVisibilityToggler(parent);
        toggler.setToggleCombination(toggleCombination);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, toggler);
    }
}

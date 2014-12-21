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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * An {@link javafx.event.EventHandler} that toggles between the currently active scene and a different scene
 * when a key combination is pressed.
 * The default key combination is Ctrl+` (back tick, usually above tab).
 *
 * @author Yevgeny Krasik
 */
// FIXME: BUG: When doing a sceneToggle and the cursor was over the textArea, the cursor remaings the text cursor.
public class SceneToggler implements EventHandler<KeyEvent> {
    private static final KeyCombination DEFAULT_KEY_COMBINATION = KeyCombination.keyCombination("Ctrl+`");

    private final Stage stage;
    private final Scene otherScene;

    private Scene prevScene;
    private KeyCombination toggleCombination = DEFAULT_KEY_COMBINATION;

    public SceneToggler(Stage stage, Parent parent) {
        this(stage, new Scene(Objects.requireNonNull(parent)));
    }

    public SceneToggler(Stage stage, Scene otherScene) {
        this.stage = Objects.requireNonNull(stage);
        this.otherScene = Objects.requireNonNull(otherScene);
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
            final Scene currentScene = stage.getScene();
            if (currentScene == otherScene) {
                deactivate();
            } else {
                activate();
            }
        }
    }

    private void activate() {
        prevScene = stage.getScene();
        stage.setScene(otherScene);
    }

    private void deactivate() {
        stage.setScene(prevScene);
        prevScene = null;
    }

    /**
     * Register a {@link SceneToggler} as an event filter with the {@link javafx.stage.Stage}.
     * Will toggle on the default key combination of Ctrl+`
     *
     * @param stage Stage to register on.
     * @param parent The parent to show upon toggle.
     */
    public static void register(Stage stage, Parent parent) {
        register(stage, parent, DEFAULT_KEY_COMBINATION);
    }

    /**
     * Register a {@link SceneToggler} as an event filter with the {@link javafx.stage.Stage}.
     * Will toggle on the supplied key combination.
     *
     * @param stage Stage to register on.
     * @param parent The parent to show upon toggle.
     * @param toggleCombination The key combination to toggle on.
     */
    public static void register(Stage stage, Parent parent, KeyCombination toggleCombination) {
        final SceneToggler toggler = new SceneToggler(stage, parent);
        toggler.setToggleCombination(toggleCombination);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, toggler);
    }
}

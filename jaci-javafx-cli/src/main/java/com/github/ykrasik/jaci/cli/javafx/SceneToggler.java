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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.NonNull;

/**
 * An {@link EventHandler} that toggles between the currently active scene and a different scene
 * when a key combination is pressed.
 * The default key combination is Ctrl+` (back tick, usually above tab).
 *
 * @author Yevgeny Krasik
 */
// FIXME: When doing a sceneToggle and the cursor is over the textArea, the cursor remains the text cursor.
public class SceneToggler implements EventHandler<KeyEvent> {
    private static final KeyCombination DEFAULT_KEY_COMBINATION = KeyCombination.keyCombination("Ctrl+`");

    private final Stage stage;
    private final Scene scene;

    private Scene prevScene;
    private KeyCombination toggleCombination = DEFAULT_KEY_COMBINATION;

    public SceneToggler(Stage stage, @NonNull Parent parent) {
        this(stage, new Scene(parent));
    }

    public SceneToggler(@NonNull Stage stage, @NonNull Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

    /**
     * Set a new toggle keyCombination.
     *
     * @param toggleCombination New keyCombination to set.
     */
    public void setToggleCombination(@NonNull KeyCombination toggleCombination) {
        this.toggleCombination = toggleCombination;
    }

    @Override
    public void handle(KeyEvent event) {
        if (toggleCombination.match(event)) {
            final Scene currentScene = stage.getScene();
            if (currentScene == scene) {
                deactivate();
            } else {
                activate();
            }
        }
    }

    private void activate() {
        prevScene = stage.getScene();
        stage.setScene(scene);

        // This is a workaround for an annoying bug (observed with JDK7u75).
        // When a scene is made visible by the stage, it is not resized to fill the stage automatically,
        // but only if the stage is somehow changed.
        // So... here is a fake change.
        final boolean resizable = stage.isResizable();
        stage.setResizable(!resizable);
        stage.setResizable(resizable);
    }

    private void deactivate() {
        stage.setScene(prevScene);
        prevScene = null;
    }

    /**
     * Register a {@link SceneToggler} as an event filter with the {@link Stage}.
     * Will toggle on the default key combination of Ctrl+`
     *
     * @param stage Stage to register on.
     * @param parent The parent to show upon toggle.
     * @return The sceneToggler, in case a different key combination is desired.
     *
     * @see #setToggleCombination(KeyCombination)
     */
    public static SceneToggler register(Stage stage, Parent parent) {
        final SceneToggler toggler = new SceneToggler(stage, parent);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, toggler);
        return toggler;
    }
}

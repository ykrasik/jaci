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

package com.github.ykrasik.jemi.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.Objects;

/**
 * An {@link InputListener} that should be registered with a {@link com.badlogic.gdx.scenes.scene2d.Stage} as a listener
 * {@link com.badlogic.gdx.scenes.scene2d.Stage#addListener(com.badlogic.gdx.scenes.scene2d.EventListener)}.
 * Will toggle the console on and off according to {@link InputEvent}s.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Separate this into VisibilityToggler and StageToggler (wraps an InputHandler and toggles between different stages).
public class LibGdxCliToggler extends InputListener {
    private final LibGdxCli console;

    public LibGdxCliToggler(LibGdxCli console) {
        this.console = Objects.requireNonNull(console);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (shouldToggle(keycode)) {
            toggle();
            event.cancel();
            return true;
        } else {
            return false;
        }
    }

    private void toggle() {
        console.setVisible(!console.isVisible());
    }

    /**
     * The default toggle combination is Ctrl+` (back tick, usually above tab).
     * Can be overridden by subclasses that want to toggle on a different combination.<br>
     * If overriding, it is recommended to not use any text keys as the toggles but combine them with another key.
     * For example, don't use `, use Ctrl+` instead, because ` is a text character  that will be printed onto the
     * command line when it regains focus, but Ctrl+` isn't.
     *
     * @param keycode keycode that was pressed.
     * @return True if the console should be toggled on this event.
     */
    protected boolean shouldToggle(int keycode) {
        return keycode == Keys.GRAVE && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
    }
}
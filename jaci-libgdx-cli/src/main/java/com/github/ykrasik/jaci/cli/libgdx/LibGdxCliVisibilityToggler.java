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

package com.github.ykrasik.jaci.cli.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import lombok.NonNull;

/**
 * An {@link InputListener} that should be registered with a {@link com.badlogic.gdx.scenes.scene2d.Stage} as a listener
 * {@link com.badlogic.gdx.scenes.scene2d.Stage#addListener(com.badlogic.gdx.scenes.scene2d.EventListener)}.
 * Will toggle the CLI visibility on and off according to {@link InputEvent}s.
 * By default, toggles on the default combination of Ctrl+` (aka tilda, back-tick, grave).
 * If a different toggle combination is desired, subclass this class and override {@link #shouldToggle(int)}.
 *
 * @author Yevgeny Krasik
 */
// TODO: Add A LibGdxCliScreenToggler
public class LibGdxCliVisibilityToggler extends InputListener {
    private final LibGdxCli cli;

    public LibGdxCliVisibilityToggler(@NonNull LibGdxCli cli) {
        this.cli = cli;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (shouldToggle(keycode)) {
            cli.toggleVisibility();
            event.cancel();
            return true;
        } else {
            return false;
        }
    }

    /**
     * The default toggle combination is Ctrl+` (back tick, usually above tab).
     * Can be overridden by subclasses that want to toggle on a different combination.<br>
     * If overriding, it is recommended not to use any text keys as the toggles but combine them a meta-key.
     * For example, don't use `, use Ctrl+` instead, because ` is a text character that will be printed onto the
     * command line when it regains focus, but Ctrl+` won't.
     *
     * @param keycode keycode that was pressed.
     * @return {@code true} if the console should be toggled on this event.
     */
    protected boolean shouldToggle(int keycode) {
        return keycode == Keys.GRAVE && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
    }
}
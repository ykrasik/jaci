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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.Objects;

/**
 * An {@link InputListener} that should be registered with a {@link com.badlogic.gdx.scenes.scene2d.Stage} as a listener
 * {@link com.badlogic.gdx.scenes.scene2d.Stage#addListener(com.badlogic.gdx.scenes.scene2d.EventListener)}.
 * Will toggle an actor's visibility on and off according to {@link InputEvent}s.
 * By default, toggles on the default key of ` (aka tilda, back-tick, grave).
 * If a different toggle combination is desired, subclass this class and override {@link #shouldToggle(int)}.
 *
 * @deprecated Use {@link com.github.ykrasik.jaci.cli.libgdx.input.KeyCombinationProcessor} instead.
 *
 * @author Yevgeny Krasik
 */
// TODO: Add A LibGdxCliScreenToggler
@Deprecated
public class LibGdxVisibilityToggler extends InputListener {
    private final Actor actor;

    public LibGdxVisibilityToggler(Actor actor) {
        this.actor = Objects.requireNonNull(actor, "actor");
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (shouldToggle(keycode)) {
            actor.setVisible(!actor.isVisible());
            event.cancel();
            return true;
        } else {
            return false;
        }
    }

    /**
     * The default toggle key is ` (back tick, usually above tab).
     * Can be overridden by subclasses that want to toggle on a different combination.
     *
     * @param keycode keycode that was pressed.
     * @return {@code true} if the actor's visibility should be toggled on this keycode.
     */
    protected boolean shouldToggle(int keycode) {
        return keycode == Keys.GRAVE;
    }
}
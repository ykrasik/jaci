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

package com.github.ykrasik.jerminal.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.github.ykrasik.jerminal.api.Console;

import java.util.Objects;

/**
 * Links input events to {@link com.github.ykrasik.jerminal.api.Console} events.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxConsoleDriver extends InputListener {
    private final Console console;

    public LibGdxConsoleDriver(Console console) {
        this.console = Objects.requireNonNull(console);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        switch (keycode) {
            case Keys.ENTER: console.execute(); return true;
            case Keys.TAB: console.assist(); return true;
            case Keys.DPAD_UP: console.setPrevCommandLineFromHistory(); return true;
            case Keys.DPAD_DOWN: console.setNextCommandLineFromHistory(); return true;
            case Keys.Z:
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    console.clearCommandLine();
                    return true;
                }
        }

        return false;
    }
}

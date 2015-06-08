/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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
import com.github.ykrasik.jaci.cli.Cli;
import lombok.NonNull;

/**
 * Links LibGdx input events to {@link Cli} events.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCliInputListener extends InputListener {
    private final Cli cli;

    public LibGdxCliInputListener(@NonNull Cli cli) {
        this.cli = cli;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        switch (keycode) {
            case Keys.ENTER:
                cli.execute();
                return true;

            case Keys.TAB:
                cli.assist();
                return true;

            case Keys.DPAD_UP:
                cli.setPrevCommandLineFromHistory();
                return true;

            case Keys.DPAD_DOWN:
                cli.setNextCommandLineFromHistory();
                return true;

            case Keys.Z:
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    cli.clearCommandLine();
                    return true;
                }
        }

        return false;
    }
}

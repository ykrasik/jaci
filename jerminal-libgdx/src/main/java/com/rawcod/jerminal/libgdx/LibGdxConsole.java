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

package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.ykrasik.jerminal.api.Shell;

/**
 * A Jerminal console based on libGdx (http://libgdx.badlogicgames.com/).<br>
 * The console is implemented as a {@link Stage} that uses a {@link LibGdxTerminal} as a display.<br>
 *
 * <p>The console is activated/deactivated by a configurable toggle button. A {@link ConsoleActivationListener} can
 * be set to be called every time the console is activated/deactivated.</p><br>
 *
 * <p>In order to work, the console must be hooked into an application's main loop and receive
 * {@link #draw()} and {@link #act(float)} calls.</p>
 *
 * @author Yevgeny Krasik
 */
public class LibGdxConsole extends Stage {
    private static final int DISABLED_KEY_CODE = -2;

    private final LibGdxTerminal terminal;
    private final Shell shell;

    private final int toggleKeycode;

    public LibGdxConsole(LibGdxTerminal terminal, Shell shell, int toggleKeycode) {
        this.toggleKeycode = toggleKeycode > 0 ? toggleKeycode : DISABLED_KEY_CODE;
        this.terminal = terminal;
        this.shell = shell;
    }

    @Override
    public void draw() {
        terminal.draw();
    }

    @Override
    public void act(float delta) {
        terminal.act(delta);
    }

    public void resize(int width, int height) {
        terminal.setViewport(width, height);
    }

    public void activate() {
        terminal.activate();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == toggleKeycode) {
            terminal.toggle();
            return true;
        }

        if (!terminal.isActive()) {
            return false;
        }

        switch (keycode) {
            case Keys.DPAD_UP:
                shell.showPrevCommandLine();
                return true;
            case Keys.DPAD_DOWN:
                shell.showNextCommandLine();
                return true;
            case Keys.ENTER:
                shell.execute(terminal.readCommandLine());
                return true;
            case Keys.TAB:
                shell.autoComplete(terminal.readCommandLineUntilCursor());
                return true;
            case Keys.Z:
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    shell.clearCommandLine();
                    return true;
                }
        }

        return terminal.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return terminal.isActive() && terminal.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return terminal.isActive() && terminal.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return terminal.isActive() && terminal.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return terminal.isActive() && terminal.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return terminal.isActive() && terminal.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return terminal.isActive() && terminal.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return terminal.isActive() && terminal.scrolled(amount);
    }
}

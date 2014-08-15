package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.github.ykrasik.jerminal.api.Shell;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public class LibGdxConsole implements InputProcessor {
    private final LibGdxTerminal terminal;
    private final Shell shell;

    private final int toggleKeycode;

    public LibGdxConsole(LibGdxTerminal terminal, Shell shell, int toggleKeycode) {
        this.toggleKeycode = toggleKeycode > 0 ? toggleKeycode : -2;
        this.terminal = terminal;
        this.shell = shell;
    }

    public void draw() {
        terminal.draw();
    }

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

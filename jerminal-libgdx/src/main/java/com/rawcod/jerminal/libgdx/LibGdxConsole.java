package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.rawcod.jerminal.Shell;
import com.rawcod.jerminal.shell.ShellManager;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public class LibGdxConsole implements InputProcessor {
    private final int toggleKeycode;

    private final LibGdxTerminal terminal;
    private final Shell shell;

    private ConsoleActivationListener listener;

    private boolean debug;
    private boolean active;

    public LibGdxConsole(float width,
                         float height,
                         int toggleKeycode,
                         int maxBufferEntries,
                         int maxCommandHistory,
                         ShellManager manager,
                         LibGdxConsoleWidgetFactory widgetFactory) {
        this.toggleKeycode = toggleKeycode > 0 ? toggleKeycode : -2;

        this.terminal = new LibGdxTerminal(width, height, maxBufferEntries, widgetFactory, this);
        this.shell = new Shell(manager, terminal, maxCommandHistory);
    }

    public void setListener(ConsoleActivationListener listener) {
        this.listener = listener;
    }

    public boolean isActive() {
        return active;
    }

    public void toggle() {
        if (!active) {
            activate();
        } else {
            deactivate();
        }
    }

    public void activate() {
        if (active) {
            return;
        }

        active = true;
        terminal.activate();

        // Notify listener
        if (listener != null) {
            listener.activated();
        }
    }

    public void deactivate() {
        if (!active) {
            return;
        }

        active = false;
        terminal.deactivate();

        // Notify listener
        if (listener != null) {
            listener.deactivated();
        }
    }

    public void draw() {
        if (!active) {
            return;
        }
        terminal.draw();

        if (debug) {
            Table.drawDebug(terminal);
        }
    }

    public void act(float delta) {
        if (!active) {
            return;
        }
        terminal.act(delta);
    }

    public void resize(int width, int height) {
        terminal.setViewport(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == toggleKeycode) {
            toggle();
            return true;
        }
        if (!active) {
            return false;
        }

        switch (keycode) {
            case Keys.DPAD_UP:
                shell.showPrevCommand();
                return true;
            case Keys.DPAD_DOWN:
                shell.showNextCommand();
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
        return active && terminal.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return active && terminal.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return active && terminal.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return active && terminal.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return active && terminal.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return active && terminal.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return active && terminal.scrolled(amount);
    }

    public void debug() {
        this.debug = !this.debug;
    }
}

package com.github.ykrasik.jerminal.libgdx.impl;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.ykrasik.jerminal.api.display.terminal.DefaultTerminalGuiController;

import java.util.Objects;

/**
 * A specialized version of a {@link DefaultTerminalGuiController} for LibGdx.
 * Wraps the 'current path' {@link Label}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxTerminalGuiController extends DefaultTerminalGuiController {
    private final Label currentPathLabel;

    public LibGdxTerminalGuiController(Label currentPathLabel) {
        this.currentPathLabel = Objects.requireNonNull(currentPathLabel);
    }

    @Override
    protected void doSetWorkingDirectory(String path) {
        currentPathLabel.setText(path);
    }
}

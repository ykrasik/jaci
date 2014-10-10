package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.output.terminal.Terminal;
import javafx.scene.control.TextArea;

import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
public class JavaFxTerminal implements Terminal {
    private final TextArea textArea;

    public JavaFxTerminal(TextArea textArea) {
        this.textArea = Objects.requireNonNull(textArea);
    }

    @Override
    public void begin() {

    }

    @Override
    public void end() {

    }

    @Override
    public void print(String text) {
        textArea.appendText(text);
    }

    @Override
    public void printError(String text) {
        // TODO: Make this red.
        textArea.appendText(text);
    }
}

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

package com.github.ykrasik.jerminal.libgdx.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.ykrasik.jerminal.api.display.terminal.Terminal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * A {@link Terminal} implemented as a {@link Table} of {@link Label}.<br>
 * Keeps a configurable maximum number of lines.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxTerminal extends Table implements Terminal {
    private final Skin skin;
    private final int maxTerminalEntries;

    private final Table buffer;
    private final ScrollPane scrollPane;
    private final Deque<Label> bufferEntries;

    // FIXME: Refactor
    public LibGdxTerminal(Skin skin, int maxTerminalEntries) {
        this.skin = Objects.requireNonNull(skin);
        this.maxTerminalEntries = maxTerminalEntries;

        // Create a buffer to hold out text labels.
        this.buffer = new Table();
        buffer.setName("terminalBuffer");
        buffer.bottom().left();
        buffer.debug();

        // Wrap the buffer in a scrollpane.
        scrollPane = new ScrollPane(buffer);
        scrollPane.setName("terminalBufferScrollPane");
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setupOverscroll(0, 0, 0);
        scrollPane.setFillParent(true);
        updateScroll();

        // Buffer history.
        this.bufferEntries = new ArrayDeque<>(maxTerminalEntries);

        add(scrollPane);
    }

    @Override
    public void begin() {
        // Nothing to do here.
    }

    @Override
    public void end() {
        // Nothing to do here.
    }

    @Override
    public void println(String text) {
        println(text, Color.WHITE);
    }

    @Override
    public void errorPrintln(String text) {
        println(text, Color.PINK);
    }

    public void println(String text, Color color) {
        final Label label = new Label(text + '\n', skin, "terminalEntry");
        label.setColor(color);
        label.setWrap(true);
        addLabel(label);
    }

    private void addLabel(Label newEntry) {
        if (bufferEntries.size() == maxTerminalEntries) {
            final Label lastEntry = bufferEntries.removeFirst();
            buffer.removeActor(lastEntry);
        }

        bufferEntries.addLast(newEntry);
        // TODO: Why expandX?
        buffer.add(newEntry).left().fillX().expandX();

        updateScroll();
    }

    private void updateScroll() {
        scrollPane.layout();
        scrollPane.setScrollPercentY(1);
        scrollPane.updateVisualScroll();
    }
}

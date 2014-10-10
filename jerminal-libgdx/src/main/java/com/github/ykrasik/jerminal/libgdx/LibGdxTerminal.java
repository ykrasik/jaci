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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.ykrasik.jerminal.api.output.terminal.Terminal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The display part of the console. This is where all text is printed.<br>
 * Keeps a configurable maximum number of lines.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxTerminal extends Table implements Terminal {
    private final LibGdxConsoleWidgetFactory widgetFactory;
    private final int maxBufferEntries;

    private final Table buffer;
    private final ScrollPane scrollPane;
    private final Deque<Label> bufferEntries;

    public LibGdxTerminal(LibGdxConsoleWidgetFactory widgetFactory, int maxBufferEntries) {
        this.widgetFactory = widgetFactory;
        this.maxBufferEntries = maxBufferEntries;

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
        this.bufferEntries = new ArrayDeque<>(maxBufferEntries);

        setName("terminal");
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
    public void print(String text) {
        print(text, Color.WHITE);
    }

    @Override
    public void printError(String text) {
        print(text, Color.PINK);
    }

    public void print(String text, Color color) {
        final Label label = widgetFactory.createBufferEntryLabel(text);
        label.setColor(color);
        addLabel(label);
    }

    private void addLabel(Label newEntry) {
        if (bufferEntries.size() == maxBufferEntries) {
            final Label lastEntry = bufferEntries.removeFirst();
            buffer.removeActor(lastEntry);
        }

        newEntry.setWrap(true);
        bufferEntries.addLast(newEntry);
        buffer.add(newEntry).left().fillX().expandX();

        updateScroll();
    }

    private void updateScroll() {
        scrollPane.layout();
        scrollPane.setScrollPercentY(1);
        scrollPane.updateVisualScroll();
    }
}

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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The display part of the {@link LibGdxTerminal}. This is where all text is printed.<br>
 * Keeps a configurable maximum number of lines.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxTerminalBuffer extends Table {
    private final int maxBufferEntries;
    private final LibGdxConsoleWidgetFactory widgetFactory;
    private final Deque<Label> bufferEntries;

    public LibGdxTerminalBuffer(int maxBufferEntries, LibGdxConsoleWidgetFactory widgetFactory) {
        this.maxBufferEntries = maxBufferEntries;
        this.widgetFactory = widgetFactory;
        this.bufferEntries = new ArrayDeque<>(maxBufferEntries);
    }

    public void println(String text) {
        final Label newEntry = widgetFactory.createBufferEntryLabel(text);
        addLabel(newEntry);
    }

    public void println(String text, Color color) {
        final Label label = widgetFactory.createBufferEntryLabel(text);
        label.setColor(color);
        addLabel(label);
    }

    private void addLabel(Label newEntry) {
        if (bufferEntries.size() == maxBufferEntries) {
            final Label lastEntry = bufferEntries.removeFirst();
            this.removeActor(lastEntry);
        }

        newEntry.setWrap(true);
        bufferEntries.addLast(newEntry);
        this.add(newEntry).left().fillX().expandX();
    }
}

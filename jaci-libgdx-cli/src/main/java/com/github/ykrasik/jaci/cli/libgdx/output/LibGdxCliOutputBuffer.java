/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.cli.libgdx.output;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

/**
 * A LibGdx implementation of a 'terminal screen'.
 * Implemented as a {@link Table} of {@link Label}, each Label representing a single line.
 * Keeps a maximum amount of lines.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCliOutputBuffer extends Table {
    private final Skin skin;
    private final int maxBufferEntries;

    private final Table buffer;
    private final ScrollPane scrollPane;
    private final Queue<Label> bufferEntries;

    /**
     * @param skin Skin to use for the lines.
     *             Must contain a {@link LabelStyle} called 'outputEntry' that will be used to style the lines.
     * @param maxBufferEntries Maximum amount of lines to store.
     */
    public LibGdxCliOutputBuffer(Skin skin, int maxBufferEntries) {
        this.skin = Objects.requireNonNull(skin, "skin");
        this.maxBufferEntries = maxBufferEntries;

        // Create a buffer to hold out text labels.
        this.buffer = new Table();
        buffer.setName("outputBuffer");
        buffer.bottom().left();

        // Wrap the buffer in a scrollpane.
        scrollPane = new ScrollPane(buffer);
        scrollPane.setName("outputBufferScrollPane");
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setupOverscroll(0, 0, 0);
        scrollPane.setFillParent(true);
        updateScroll();

        // Buffer history.
        this.bufferEntries = new ArrayDeque<>(maxBufferEntries);

        add(scrollPane);
    }

    /**
     * Add a single line to this buffer.
     * The line may contain a '\n' character, and it will be honored, but this is discouraged.
     *
     * @param text Line text.
     * @param color Line color.
     */
    public void println(String text, Color color) {
        final Label label = new Label(text, skin, "outputEntry");
        label.setColor(color);
        label.setWrap(true);
        addLabel(label);
    }

    private void addLabel(Label newEntry) {
        if (bufferEntries.size() == maxBufferEntries) {
            final Label lastEntry = bufferEntries.poll();
            buffer.removeActor(lastEntry);
        }

        bufferEntries.add(newEntry);
        buffer.add(newEntry).left().row();

        updateScroll();
    }

    private void updateScroll() {
        // Set the scroll to the bottom of the pane.
        scrollPane.layout();
        scrollPane.setScrollPercentY(1);
        scrollPane.updateVisualScroll();
    }
}

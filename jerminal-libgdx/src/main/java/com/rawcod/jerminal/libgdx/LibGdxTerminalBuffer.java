package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * User: ykrasik
 * Date: 04/01/14
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

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

package com.github.ykrasik.jemi.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.github.ykrasik.jemi.cli.CliShell;
import com.github.ykrasik.jemi.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jemi.cli.hierarchy.CliCommandHierarchyImpl;
import com.github.ykrasik.jemi.cli.output.CliOutput;
import com.github.ykrasik.jemi.cli.output.CliSerializer;
import com.github.ykrasik.jemi.core.hierarchy.CommandHierarchy;
import lombok.NonNull;

/**
 * A {@link Table} that keeps a list of listeners that are called whenever that table is made visible or invisible with
 * {@link #setVisible(boolean)}.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class LibGdxCli extends Table {
    private final Array<VisibleListener> visibleListeners = new Array<>(2);

    public LibGdxCli(@NonNull Skin skin,
                     @NonNull CommandHierarchy hierarchy,
                     int maxBufferEntries,
                     int maxCommandHistory) {
        super(skin);

        final LibGdxCliOutputBuffer buffer = new LibGdxCliOutputBuffer(skin, maxBufferEntries);
        buffer.setName("buffer");
        buffer.bottom().left();

        final TextField commandLine = new TextField("", skin, "commandLine");
        commandLine.setName("commandLine");

        final Label workingDirectory = new Label("", skin, "workingDirectory");
        workingDirectory.setName("workingDirectory");

        final CliOutput output = new LibGdxCliOutput(buffer, commandLine, workingDirectory);

        final CliSerializer serializer = new LibGdxCliSerializer();

        final CliCommandHierarchy cliCommandHierarchy = CliCommandHierarchyImpl.from(hierarchy);
        final CliShell shell = new CliShell(cliCommandHierarchy, output, serializer, maxCommandHistory);

        this.addListener(new LibGdxCliInputListener(shell, commandLine));

        // A close button.
        // TODO: This should be a button, not a text button.
        final Button closeButton = new TextButton("X", skin, "closeCliButton");
        closeButton.padRight(15).padLeft(15);
        closeButton.setName("closeButton");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        // Some layout.

        final Table workingDirectoryTable = new Table(skin);
        workingDirectoryTable.setName("workingDirectoryTable");
        workingDirectoryTable.setBackground("workingDirectoryBackground");
        workingDirectoryTable.add(workingDirectory).fill().padLeft(3).padRight(5);

        // The bottom row contains the current path, command line and a close button.
        final Table bottomRow = new Table(skin);
        bottomRow.setName("bottomRow");
        bottomRow.setBackground("bottomRowBackground");
        bottomRow.add(workingDirectoryTable).fill();
        bottomRow.add(commandLine).fill().expandX();
        bottomRow.add(closeButton).fill();

        this.setName("cli");
        this.setBackground("cliBackground");
        this.addVisibleListener(new VisibleListener() {
            @Override
            public void onVisibleChange(boolean wasVisible, boolean isVisible) {
                if (!wasVisible && isVisible) {
                    final Stage stage = getStage();
                    if (stage != null) {
                        stage.setKeyboardFocus(commandLine);
                    }
                }
            }
        });

        this.pad(0);
        this.add(buffer).fill().expand();
        this.row();
        this.add(bottomRow).fill();
        this.top().left();
    }

    public void addVisibleListener(VisibleListener listener) {
        visibleListeners.add(listener);
    }

    public void removeVisibleListener(VisibleListener listener) {
        visibleListeners.removeValue(listener, true);
    }

    @Override
    public void setVisible(boolean visible) {
        final boolean wasVisible = isVisible();
        for (VisibleListener listener : visibleListeners) {
            listener.onVisibleChange(wasVisible, visible);
        }
        super.setVisible(visible);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            // Call the listeners when we're first added to a stage.
            final boolean isVisible = isVisible();
            final boolean wasVisible = !isVisible;
            for (VisibleListener listener : visibleListeners) {
                listener.onVisibleChange(wasVisible, isVisible);
            }
        }
    }

    // TODO: JavaDoc
    public static class Builder {
        private final CommandHierarchy hierarchy;

        private Skin skin;
        private int maxBufferEntries = 100;
        private int maxCommandHistory = 30;

        // TODO: JavaDoc
        public Builder(@NonNull CommandHierarchy hierarchy) {
            this.hierarchy = hierarchy;
        }

        public LibGdxCli build() {
            return new LibGdxCli(getSkin(), hierarchy, maxBufferEntries, maxCommandHistory);
        }

        private Skin getSkin() {
            if (skin != null) {
                return skin;
            }

            // Default skin.
            return new Skin(Gdx.files.classpath("com/github/ykrasik/jemi/libgdx/default_cli.cfg"));
        }

        /**
         * Sets the maximum amount of output buffer entries to keep.
         *
         * @param maxBufferEntries Max output buffer entries to keep.
         * @return this, for chaining.
         */
        public Builder setMaxBufferEntries(int maxBufferEntries) {
            this.maxBufferEntries = maxBufferEntries;
            return this;
        }

        /**
         * @param maxCommandHistory Max command history to keep.
         * @return this, for chaining.
         */
        public Builder setMaxCommandHistory(int maxCommandHistory) {
            this.maxCommandHistory = maxCommandHistory;
            return this;
        }

        /**
         * @param skin Skin to use.
         * @return this, for chaining.
         */
        // TODO: JavaDoc
        public Builder setSkin(Skin skin) {
            this.skin = skin;
            return this;
        }
    }
}

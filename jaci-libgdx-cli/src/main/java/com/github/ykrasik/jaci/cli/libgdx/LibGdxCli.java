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

package com.github.ykrasik.jaci.cli.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.github.ykrasik.jaci.cli.Cli;
import com.github.ykrasik.jaci.cli.CliShell;
import com.github.ykrasik.jaci.cli.commandline.CommandLineManager;
import com.github.ykrasik.jaci.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jaci.cli.hierarchy.CliCommandHierarchyImpl;
import com.github.ykrasik.jaci.cli.libgdx.commandline.LibGdxCommandLineManager;
import com.github.ykrasik.jaci.cli.libgdx.output.LibGdxCliOutput;
import com.github.ykrasik.jaci.cli.libgdx.output.LibGdxCliOutputBuffer;
import com.github.ykrasik.jaci.cli.output.CliOutput;
import com.github.ykrasik.jaci.cli.output.CliSerializer;
import com.github.ykrasik.jaci.cli.output.DefaultCliSerializer;
import com.github.ykrasik.jaci.hierarchy.CommandHierarchyDef;

import java.util.Objects;

/**
 * A CLI implementation for LibGdx.<br>
 * <br>
 * Implemented as a {@link Table} that keeps a list of listeners that are called whenever the CLI
 * is made visible or invisible with {@link #setVisible(boolean)}, to allow the game to be paused (for example).<br>
 * <br>
 * Built with a default skin, unless a custom skin is provided:
 * A custom skin must have the following:
 * <ul>
 *     <li>
 *         A {@link LabelStyle} called 'workingDirectory' that will be used to style the 'workingDirectory' label.
 *     </li>
 *     <li>
 *         A {@link com.badlogic.gdx.scenes.scene2d.utils.Drawable} called 'workingDirectoryBackground'
 *         that will be used as the background of the 'workingDirectory' label.
 *     </li>
 *     <li>
 *         A {@link TextFieldStyle} called 'commandLine' that will be used to style the command line text field.
 *     </li>
 *     <li>
 *         A {@link TextButtonStyle} called 'closeCliButton' that will be used to style the close button.
 *     </li>
 *     <li>
 *         A {@link com.badlogic.gdx.scenes.scene2d.utils.Drawable} called 'bottomRowBackground'
 *         that will be used as the background of the 'bottom row' (working directory, command line, close button).
 *     </li>
 *     <li>
 *         A {@link LabelStyle} called 'outputEntry' that will be used to style output buffer entry lines.
 *     </li>
 *     <li>
 *         A {@link com.badlogic.gdx.scenes.scene2d.utils.Drawable} called 'cliBackground'
 *         that will be used as the background of the whole widget.
 *     </li>
 * </ul>
 *
 * Built through the {@link LibGdxCli.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCli extends Table {
    private final Array<VisibleListener> visibleListeners = new Array<>(2);

    /**
     * @param skin Skin to use.
     * @param hierarchy Command hierarchy.
     * @param maxBufferEntries Maximum amount of line entries in the buffer to keep.
     * @param maxCommandHistory Maximum amount of command history entries to keep.
     */
    private LibGdxCli(Skin skin, CliCommandHierarchy hierarchy, int maxBufferEntries, int maxCommandHistory) {
        super(Objects.requireNonNull(skin, "skin"));

        // Buffer for cli output.
        final LibGdxCliOutputBuffer buffer = new LibGdxCliOutputBuffer(skin, maxBufferEntries);
        buffer.setName("buffer");
        buffer.bottom().left();

        // Label for 'working directory'.
        final Label workingDirectory = new Label("", skin, "workingDirectory");
        workingDirectory.setName("workingDirectory");

        // The above combine into a CliOutput.
        final CliOutput output = new LibGdxCliOutput(buffer, workingDirectory);

        // TextField as command line.
        final TextField commandLine = new TextField("", skin, "commandLine");
        commandLine.setName("commandLine");
        final CommandLineManager commandLineManager = new LibGdxCommandLineManager(commandLine);

        // LibGdx seems to ignore leading \t
        final CliSerializer serializer = new DefaultCliSerializer("    ");

        // Create the shell and the actual CLI.
        final CliShell shell = new CliShell.Builder(hierarchy, output)
            .setSerializer(serializer)
            .setMaxCommandHistory(maxCommandHistory)
            .build();
        final Cli cli = new Cli(shell, commandLineManager);

        // Hook input events to CLI events.
        this.addListener(new LibGdxCliInputListener(cli));

        // A close button.
        // TODO: Make this a graphical button, not an ugly text button.
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

        // TODO: This should operate on it's own stage.
        this.addVisibleListener(new VisibleListener() {
            @Override
            public void onVisibleChange(boolean wasVisible, boolean isVisible) {
                if (!wasVisible && isVisible) {
                    setKeyboardFocus(commandLine);
                }
            }
        });

        this.pad(0);
        this.add(buffer).fill().expand();
        this.row();
        this.add(bottomRow).fill();
        this.top().left();
        this.setFillParent(true);
    }

    private void setKeyboardFocus(TextField commandLine) {
        final Stage stage = getStage();
        if (stage != null) {
            stage.setKeyboardFocus(commandLine);
        }
    }

    /**
     * Add a {@link VisibleListener} that will be called when this actor's visibility state changes -
     * it either was visible and became invisible, or the other way.
     *
     * @param listener Listener to add.
     */
    public void addVisibleListener(VisibleListener listener) {
        visibleListeners.add(listener);
    }

    /**
     * Remove a {@link VisibleListener} from this actor.
     *
     * @param listener Listener to remove.
     */
    public void removeVisibleListener(VisibleListener listener) {
        visibleListeners.removeValue(listener, true);
    }

    /**
     * Toggle the visibility of this CLI on or off.
     */
    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
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

        // Call the listeners when we're first added to a stage.
        final boolean isVisible = isVisible();
        final boolean wasVisible = !isVisible;
        for (VisibleListener listener : visibleListeners) {
            listener.onVisibleChange(wasVisible, isVisible);
        }
    }

    /**
     * A builder for a {@link LibGdxCli}.
     * Builds a CLI with a default skin, unless a custom skin is specified via {@link #setSkin(Skin)}.<br>
     * The main methods to use are {@link #processClasses(Class[])} and {@link #process(Object[])} which process
     * a class and add any annotated methods as commands to this builder.
     */
    public static class Builder {
        private final CommandHierarchyDef.Builder hierarchyBuilder = new CommandHierarchyDef.Builder();

        private Skin skin;
        private int maxBufferEntries = 100;
        private int maxCommandHistory = 30;

        /**
         * Process the classes and add any commands defined through annotations to this builder.
         * Each class must have a no-args constructor.
         *
         * @param classes Classes to process.
         * @return {@code this}, for chaining.
         */
        public Builder processClasses(Class<?>... classes) {
            hierarchyBuilder.processClasses(classes);
            return this;
        }

        /**
         * Process the objects' classes and add any commands defined through annotations to this builder.
         *
         * @param instances Objects whose classes to process.
         * @return {@code this}, for chaining.
         */
        public Builder process(Object... instances) {
            hierarchyBuilder.process(instances);
            return this;
        }

        /**
         * Set the maximum amount of output buffer entries to keep.
         *
         * @param maxBufferEntries Max output buffer entries to keep.
         * @return {@code this}, for chaining.
         */
        public Builder setMaxBufferEntries(int maxBufferEntries) {
            this.maxBufferEntries = maxBufferEntries;
            return this;
        }

        /**
         * Set the maximum amount of command history entries to keep.
         *
         * @param maxCommandHistory Max command history entries to keep.
         * @return {@code this}, for chaining.
         */
        public Builder setMaxCommandHistory(int maxCommandHistory) {
            this.maxCommandHistory = maxCommandHistory;
            return this;
        }

        /**
         * Set the skin to use.<br>
         * A custom skin must have the following:
         * <ul>
         *     <li>
         *         A {@link LabelStyle} called 'workingDirectory' that will be used to style the 'workingDirectory' label.
         *     </li>
         *     <li>
         *         A {@link com.badlogic.gdx.scenes.scene2d.utils.Drawable} called 'workingDirectoryBackground'
         *         that will be used as the background of the 'workingDirectory' label.
         *     </li>
         *     <li>
         *         A {@link TextFieldStyle} called 'commandLine' that will be used to style the command line text field.
         *     </li>
         *     <li>
         *         A {@link TextButtonStyle} called 'closeCliButton' that will be used to style the close button.
         *     </li>
         *     <li>
         *         A {@link com.badlogic.gdx.scenes.scene2d.utils.Drawable} called 'bottomRowBackground'
         *         that will be used as the background of the 'bottom row' (working directory, command line, close button).
         *     </li>
         *     <li>
         *         A {@link LabelStyle} called 'outputEntry' that will be used to style output buffer entry lines.
         *     </li>
         *     <li>
         *         A {@link com.badlogic.gdx.scenes.scene2d.utils.Drawable} called 'cliBackground'
         *         that will be used as the background of the whole widget.
         *     </li>
         * </ul>
         *
         * @param skin Skin to use.
         * @return {@code this}, for chaining.
         */
        public Builder setSkin(Skin skin) {
            this.skin = skin;
            return this;
        }

        /**
         * @return A {@link LibGdxCli} built out of this builder's parameters.
         */
        public LibGdxCli build() {
            final Skin skin = getSkin();
            final CliCommandHierarchy hierarchy = CliCommandHierarchyImpl.from(hierarchyBuilder.build());
            return new LibGdxCli(skin, hierarchy, maxBufferEntries, maxCommandHistory);
        }

        private Skin getSkin() {
            if (skin != null) {
                return skin;
            }

            // Default skin.
            return new Skin(Gdx.files.classpath("com/github/ykrasik/jaci/cli/libgdx/default_cli.cfg"));
        }
    }
}

/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.cli.javafx;

import com.github.ykrasik.jaci.cli.Cli;
import com.github.ykrasik.jaci.cli.CliShell;
import com.github.ykrasik.jaci.cli.commandline.CommandLineManager;
import com.github.ykrasik.jaci.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jaci.cli.hierarchy.CliCommandHierarchyImpl;
import com.github.ykrasik.jaci.cli.javafx.commandline.JavaFxCommandLineManager;
import com.github.ykrasik.jaci.cli.javafx.output.JavaFxCliOutput;
import com.github.ykrasik.jaci.cli.output.CliOutput;
import com.github.ykrasik.jaci.hierarchy.CommandHierarchyDef;
import com.github.ykrasik.jaci.reflection.JavaReflectionAccessor;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Objects;

/**
 * A CLI builder for JavaFx.<br>
 * <br>
 * Uses a default layout, unless a custom layout is provided.
 * A custom layout must have the following:
 * <ul>
 *     <li>A {@link TextArea} with a fxml id 'cliOutput' that will be used as the terminal output.</li>
 *     <li>A {@link Label} with a fxml id 'workingDirectory' that will be used to display the 'workingDirectory' label.</li>
 *     <li>A {@link TextField} with a fxml id 'commandLine' that will be used to read and display the command line.</li>
 * </ul>
 *
 * The default layout may also be customized with css:
 * <ul>
 *     <li>The output is a {@link TextArea} with a css id '#cliOutput'</li>
 *     <li>The working directory is a {@link Label} with a css id '#workingDirectory'</li>
 *     <li>The command line is a {@link TextField} with a css id '#commandLine'</li>
 * </ul>
 *
 * @author Yevgeny Krasik
 */
public class JavaFxCliBuilder {
    static {
        // Set reflection to the Java API.
        JavaReflectionAccessor.install();
    }

    private final CommandHierarchyDef.Builder hierarchyBuilder = new CommandHierarchyDef.Builder();

    // TODO: Add Max textArea size.
    private URL fxmlUrl;
    private int maxCommandHistory = 30;

    /**
     * Process the classes and add any commands defined through annotations to this builder.
     * Each class must have a no-args constructor.
     *
     * @param classes Classes to process.
     * @return {@code this}, for chaining.
     */
    public JavaFxCliBuilder processClass(Class<?>... classes) {
        hierarchyBuilder.processClasses(classes);
        return this;
    }

    /**
     * Process the objects' classes and add any commands defined through annotations to this builder.
     *
     * @param instances Objects whose classes to process.
     * @return {@code this}, for chaining.
     */
    public JavaFxCliBuilder process(Object... instances) {
        hierarchyBuilder.process(instances);
        return this;
    }

    /**
     * Set the maximum amount of command history entries to keep.
     *
     * @param maxCommandHistory Max command history entries to keep.
     * @return {@code this}, for chaining.
     */
    public JavaFxCliBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    /**
     * Set a path to a .fxml file containing a custom layout.
     * A custom layout must have the following:
     * <ul>
     *     <li>A {@link TextArea} with a fxml id 'cliOutput' that will be used as the terminal output.</li>
     *     <li>A {@link Label} with a fxml id 'workingDirectory' that will be used to display the 'workingDirectory' label.</li>
     *     <li>A {@link TextField} with a fxml id 'commandLine' that will be used to read and display the command line.</li>
     * </ul>
     *
     * @param fxmlPath Path to .fxml file containing the custom layout.
     * @return {@code this}, for chaining.
     */
    public JavaFxCliBuilder setFxmlPath(String fxmlPath) {
        return setFxmlUrl(Thread.currentThread().getContextClassLoader().getResource(fxmlPath));
    }

    /**
     * Set the url to a .fxml file containing a custom layout.
     * A custom layout must have the following:
     * <ul>
     *     <li>A {@link TextArea} with a fxml id 'cliOutput' that will be used as the terminal output.</li>
     *     <li>A {@link Label} with a fxml id 'workingDirectory' that will be used to display the 'workingDirectory' label.</li>
     *     <li>A {@link TextField} with a fxml id 'commandLine' that will be used to read and display the command line.</li>
     * </ul>
     *
     * @param fxmlUrl URL to a .fxml file containing the custom layout.
     * @return {@code this}, for chaining.
     */
    public JavaFxCliBuilder setFxmlUrl(URL fxmlUrl) {
        this.fxmlUrl = Objects.requireNonNull(fxmlUrl, "fxmlUrl");
        return this;
    }

    /**
     * @return A {@link Parent} that functions as a CLI built out of this builder's parameters.
     * @throws RuntimeException If an error occurs.
     */
    public Parent build() {
        try {
            final CliCommandHierarchy hierarchy = CliCommandHierarchyImpl.from(hierarchyBuilder.build());

            final URL fxmlUrl = getFxmlUrl();
            final FXMLLoader loader = new FXMLLoader(fxmlUrl);
            final Parent cliNode = (Parent) loader.load();

            // Buffer for cli output.
            final TextArea textArea = (TextArea) cliNode.lookup("#cliOutput");
            textArea.setFocusTraversable(false);

            // Label for 'working directory'.
            final Label workingDirectory = (Label) cliNode.lookup("#workingDirectory");

            // The above combine into a CliOutput.
            final CliOutput output = new JavaFxCliOutput(textArea, workingDirectory);

            // TextField as command line.
            final TextField commandLine = (TextField) cliNode.lookup("#commandLine");
            final CommandLineManager commandLineManager = new JavaFxCommandLineManager(commandLine);

            // Create the shell and the actual CLI.
            final CliShell shell = new CliShell.Builder(hierarchy, output)
                .setMaxCommandHistory(maxCommandHistory)
                .build();
            final Cli cli = new Cli(shell, commandLineManager);

            // Hook input events to CLI events.
            commandLine.requestFocus();
            commandLine.addEventFilter(KeyEvent.KEY_PRESSED, new JavaFxCliEventHandler(cli));

            return cliNode;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private URL getFxmlUrl() {
        if (fxmlUrl != null) {
            return fxmlUrl;
        }
        return JavaFxCliBuilder.class.getResource("default_cli.fxml");
    }
}

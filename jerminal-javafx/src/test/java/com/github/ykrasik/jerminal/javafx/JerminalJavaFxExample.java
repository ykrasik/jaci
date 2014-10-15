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

package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.Console;
import com.github.ykrasik.jerminal.api.ConsoleImpl;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.api.display.terminal.Terminal;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalDisplayDriver;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class JerminalJavaFxExample extends Application {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            doStart(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        executor.shutdownNow();
    }

    private void doStart(Stage stage) throws IOException {
        final BorderPane mainWindow = (BorderPane) loadFxml("main.fxml");
        this.scene = new Scene(mainWindow);

        stage.setTitle("Jerminal");
        stage.setScene(scene);
        stage.show();

        final TextArea textArea = findById("textArea", TextArea.class);
        textArea.setFocusTraversable(false);
        final Terminal terminal = new JavaFxTerminal(textArea);

        final ShellFileSystem fileSystem = createFileSystem();
        fileSystem.processAnnotations(AnnotationExample.class);
        final Shell shell = new Shell(fileSystem, new TerminalDisplayDriver(terminal));

        final TextField textField = findById("textField", TextField.class);
        final JavaFxCommandLineDriver commandLineDriver = new JavaFxCommandLineDriver(textField);
        final Console console = new ConsoleImpl(shell, commandLineDriver);

        textField.requestFocus();
        textField.addEventFilter(KeyEvent.KEY_PRESSED, new JavaFxConsoleDriver(console));
    }

    private Object loadFxml(String path) throws IOException {
        final URL resource = Objects.requireNonNull(getClass().getResource(path));
        final FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }

    private <T> T findById(String id, Class<T> clazz) {
        return clazz.cast(findById(id));
    }

    private Node findById(String id) {
        return scene.lookup('#' + id);
    }

    private ShellFileSystem createFileSystem() {
        return new ShellFileSystem()
            .addCommands("nested/d/1possible")
            .addCommands("nested/d/2possible")
            .addCommands("nested/dir/singlePossible")
            .addCommands("nested/dir1/singlePossible")
            .addCommands("nested/dir2/singlePossible")
            .addCommands("nested/directory/singlePossible/multiplePossible1/singlePossible")
            .addCommands("nested/directory/singlePossible/multiplePossible2/singlePossible")
            .addCommands(
                new CommandBuilder("cmd")
                    .setDescription("cmd")
                    .addParam(
                        new IntegerParamBuilder("mandatoryInt")
                            .setDescription("This int is mandatory!")
                            .build()
                    )
                    .addParam(
                        new BooleanParamBuilder("optionalBool")
                            .setDescription("This boolean is optional...")
                            .setOptional(false)
                            .build()
                    )
                    .setExecutor(new CommandExecutor() {
                        @Override
                        public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                            final int integer = args.getInt("mandatoryInt");
                            final boolean bool = args.getBool("optionalBool");
                            outputPrinter.println("yay: int = %d, bool = %s", integer, bool);
                        }
                    })
                    .build(),
                new CommandBuilder("nestCommand")
                    .setDescription("test Command")
                    .addParam(
                        new StringParamBuilder("nested")
                            .setConstantAcceptableValues("test1", "value2", "param3", "long string")
                            .build()
                    )
                    .addParam(
                        new BooleanParamBuilder("booleany")
                            .build()
                    )
                    .setExecutor(new CommandExecutor() {
                        @Override
                        public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                            final String str = args.getString("nested");
                            final boolean bool = args.getBool("booleany");
                            outputPrinter.println("yay: string = %s, bool = %s", str, bool);
                        }
                    })
                    .build()
            );
    }
}

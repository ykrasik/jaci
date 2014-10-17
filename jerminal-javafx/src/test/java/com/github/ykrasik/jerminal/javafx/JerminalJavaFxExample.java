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

import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class JerminalJavaFxExample extends Application {
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
    }

    private void doStart(Stage stage) throws IOException {
        final ShellFileSystem fileSystem = createFileSystem();

        final Parent console = new ConsoleBuilder(fileSystem).build();
        final Scene scene = new Scene(console, 1280, 720);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new ConsoleToggler(console));

        stage.setTitle("Jerminal");
        stage.setScene(scene);
        stage.show();
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
            )
            .processAnnotations(AnnotationExample.class);
    }
}

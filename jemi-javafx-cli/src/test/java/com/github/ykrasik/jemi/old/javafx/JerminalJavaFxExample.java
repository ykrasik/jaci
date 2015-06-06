///******************************************************************************
// * Copyright (C) 2014 Yevgeny Krasik                                          *
// *                                                                            *
// * Licensed under the Apache License, Version 2.0 (the "License");            *
// * you may not use this file except in compliance with the License.           *
// * You may obtain a copy of the License at                                    *
// *                                                                            *
// * http://www.apache.org/licenses/LICENSE-2.0                                 *
// *                                                                            *
// * Unless required by applicable law or agreed to in writing, software        *
// * distributed under the License is distributed on an "AS IS" BASIS,          *
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
// * See the License for the specific language governing permissions and        *
// * limitations under the License.                                             *
// ******************************************************************************/
//
//package com.github.ykrasik.jemi.old.javafx;
//
//import com.github.ykrasik.jemi.core.command.CommandArgs;
//import com.github.ykrasik.jerminal.old.command.CommandBuilder;
//import com.github.ykrasik.jemi.core.command.CommandExecutor;
//import com.github.ykrasik.jemi.api.CommandOutput;
//import com.github.ykrasik.jerminal.old.parameter.bool.BooleanParamBuilder;
//import com.github.ykrasik.jerminal.old.parameter.numeric.IntegerParamBuilder;
//import com.github.ykrasik.jerminal.old.parameter.string.StringParamBuilder;
//import com.github.ykrasik.jemi.cli.exception.ExecuteException;
//import javafx.application.Application;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
///**
// * @author Yevgeny Krasik
// */
//// FIXME: JavaDoc
//public class JerminalJavaFxExample extends Application {
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) {
//        try {
//            doStart(stage);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void stop() throws Exception {
//    }
//
//    private void doStart(Stage stage) throws IOException {
//        stage.setTitle("Jerminal");
//        stage.setWidth(1280);
//        stage.setHeight(720);
//
//        // Create a console
//        final ShellFileSystem fileSystem = createFileSystem();
//        final Parent console = new ConsoleBuilder(fileSystem).build();
//
//        // Add a console toggler. The toggler will switch between the main scene and the console scene.
//        SceneToggler.register(stage, console);
//
//        // Create a boring main scene.
//        final Pane root = new Pane();
//        root.getChildren().add(new Label("Nothing to see here"));
//        final Scene scene = new Scene(root);
//
//        stage.setScene(scene);
//
//        // This is just a hack to get the console to be toggled immediately when the application starts.
//        // Not required in production code.
//        stage.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "`", "`", KeyCode.BACK_QUOTE, false, true, false, false));
//
//        stage.show();
//    }
//
//    private ShellFileSystem createFileSystem() {
//        return new ShellFileSystem()
//            .addCommands("nested/d/1possible")
//            .addCommands("nested/d/2possible")
//            .addCommands("nested/dir/singlePossible")
//            .addCommands("nested/dir1/singlePossible")
//            .addCommands("nested/dir2/singlePossible")
//            .addCommands("nested/directory/singlePossible/multiplePossible1/singlePossible")
//            .addCommands("nested/directory/singlePossible/multiplePossible2/singlePossible")
//            .addCommands(
//                new CommandBuilder("cmd")
//                    .setDescription("cmd")
//                    .addParam(
//                        new IntegerParamBuilder("mandatoryInt")
//                            .setDescription("This int is mandatory!")
//                            .build()
//                    )
//                    .addParam(
//                        new BooleanParamBuilder("optionalBool")
//                            .setDescription("This boolean is optional...")
//                            .setOptional(false)
//                            .build()
//                    )
//                    .setExecutor(new CommandExecutor() {
//                        @Override
//                        public void execute(CommandOutput output, CommandArgs args) throws ExecuteException {
//                            final int integer = args.getInt("mandatoryInt");
//                            final boolean bool = args.getBool("optionalBool");
//                            output.message("yay: int = %d, bool = %s", integer, bool);
//                        }
//                    })
//                    .build(),
//                new CommandBuilder("nestCommand")
//                    .setDescription("test Command")
//                    .addParam(
//                        new StringParamBuilder("nested")
//                            .setConstantAcceptableValues("test1", "value2", "param3", "long string")
//                            .build()
//                    )
//                    .addParam(
//                        new BooleanParamBuilder("booleany")
//                            .build()
//                    )
//                    .setExecutor(new CommandExecutor() {
//                        @Override
//                        public void execute(CommandOutput output, CommandArgs args) throws ExecuteException {
//                            final String str = args.getString("nested");
//                            final boolean bool = args.getBool("booleany");
//                            output.message("yay: string = %s, bool = %s", str, bool);
//                        }
//                    })
//                    .build()
//            )
//            .processAnnotations(AnnotationExample.class);
//    }
//}
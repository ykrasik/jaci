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

package com.github.ykrasik.jaci.cli.javafx;

import com.github.ykrasik.jaci.commands.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Yevgeny Krasik
 */
public class JavaFxCliTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setWidth(1280);
        stage.setHeight(720);

        // Create a CLI.
        final Parent cli = new JavaFxCliBuilder()
            .processClasses(BasicCommands.class, PathCommands1.class, PathCommands2.class)
            // Can also process objects instead of classes.
            .process(new MandatoryParamsCommands(), new OptionalParamsCommands(), new StringParamCommands())
            .processClasses(EnumCommands.class, InnerClassCommands.class, NullableParamsCommands.class)
            .build();

        // Add a scene toggler.
        // The toggler will switch between the main scene and the CLI scene on a key combination (default `).
        SceneToggler.register(stage, cli);

        // Create a boring main scene.
        final Pane root = new Pane();
        root.getChildren().add(new Label("Nothing to see here"));
        final Scene scene = new Scene(root);

        stage.setScene(scene);

        // The CLI will be hidden initially, press ` to show it.
        stage.show();
    }

    @Override
    public void stop() throws Exception {

    }
}

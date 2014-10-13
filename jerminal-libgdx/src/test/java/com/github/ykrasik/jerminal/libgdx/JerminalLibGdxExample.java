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

package com.github.ykrasik.jerminal.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;

/**
 * @author Yevgeny Krasik
 */
public class JerminalLibGdxExample extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Jerminal";
        config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
        config.fullscreen = false;
        config.height -= 150;
        config.width -= 100;
        config.resizable = true;
        config.x = 50;
        config.y = 50;

        new LwjglApplication(new JerminalLibGdxExample(), config);
    }

    @Override
    public void create() {
        final ConsoleWidgetFactory widgetFactory = new TestConsoleWidgetFactory();
        final ShellFileSystem fileSystem = createFileSystem();
        final LibGdxConsoleBuilder builder = new LibGdxConsoleBuilder(fileSystem, widgetFactory);
        final LibGdxConsole console = builder.build();
        console.setFillParent(true);

        stage = new Stage();
        stage.addActor(console);

        Gdx.input.setInputProcessor(stage);
        console.activate();
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
                        .setConstantPossibleValues("test1", "value2", "param3", "long string")
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

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height);
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }

    private static class TestConsoleWidgetFactory implements ConsoleWidgetFactory {
        private final Skin skin;

        private TestConsoleWidgetFactory() {
            this.skin = new Skin(Gdx.files.internal("debug-ui.cfg"));
        }

        @Override
        public Label createBufferEntryLabel(String text) {
            return new Label(text, skin);
        }

        @Override
        public Drawable createTerminalBufferBackground() {
            return skin.getDrawable("semi-transparent-gray");
        }

        @Override
        public Drawable createConsoleBottomRowBackground() {
            return skin.getDrawable("gray");
        }

        @Override
        public Drawable createCurrentPathLabelBackground() {
            return skin.getDrawable("textfield");
        }

        @Override
        public Label createCurrentPathLabel(String currentPath) {
            return new Label(currentPath, skin);
        }

        @Override
        public TextField createInputTextField() {
            return new TextField("", skin);
        }

        @Override
        public Button createCloseButton() {
            final TextButton button = new TextButton("X", skin);
            button.setWidth(40);
            return button;
        }
    }
}

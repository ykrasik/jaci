package com.github.ykrasik.jermina.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.command.ShellCommandBuilder;
import com.github.ykrasik.jerminal.libgdx.LibGdxJerminalConsole;
import com.github.ykrasik.jerminal.libgdx.LibGdxConsoleBuilder;
import com.github.ykrasik.jerminal.libgdx.LibGdxConsoleWidgetFactory;

/**
 * User: ykrasik
 * Date: 08/01/14
 */
public class JerminalLibGdxExample extends ApplicationAdapter {
    private LibGdxJerminalConsole console;

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
        final int maxBufferEntries = 30;
        final LibGdxConsoleWidgetFactory widgetFactory = new JerminalLibGdxTestConsoleWidgetFactory();

        final LibGdxConsoleBuilder builder = new LibGdxConsoleBuilder(widgetFactory, maxBufferEntries);
        builder.setMaxCommandHistory(20)
            .setToggleKeycode(Keys.GRAVE);

        createFileSystem(builder);

        console = builder.build();

        Gdx.input.setInputProcessor(console);
        console.activate();
    }

    private void createFileSystem(LibGdxConsoleBuilder builder) {
        builder.add("nested/d/1possible");
        builder.add("nested/d/2possible");
        builder.add("nested/dir/singlePossible");
        builder.add("nested/dir1/singlePossible");
        builder.add("nested/dir2/singlePossible");
        builder.add("nested/directory/singlePossible/multiplePossible1/singlePossible");
        builder.add("nested/directory/singlePossible/multiplePossible2/singlePossible");

        builder.add(
            new ShellCommandBuilder("cmd")
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
            new ShellCommandBuilder("nestCommand")
              .setDescription("test Command")
              .addParam(
                  new StringParamBuilder("nested")
                    .setConstantPossibleValues("test1", "value2", "param3")
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
        console.setViewport(width, height);
    }

    @Override
    public void render() {
        console.draw();
        console.act();
    }

    private static class JerminalLibGdxTestConsoleWidgetFactory implements LibGdxConsoleWidgetFactory {
        private final Skin skin;

        private JerminalLibGdxTestConsoleWidgetFactory() {
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
        public TextField createInputTextField(String initialText) {
            return new TextField(initialText, skin);
        }

        @Override
        public Button createCloseButton() {
            final TextButton button = new TextButton("X", skin);
            button.setWidth(40);
            return button;
        }
    }
}

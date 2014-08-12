package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecuteContext;
import com.rawcod.jerminal.command.parameters.bool.BooleanParamBuilder;
import com.rawcod.jerminal.command.parameters.number.IntegerParamBuilder;
import com.rawcod.jerminal.command.parameters.string.StringParamBuilder;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue;

/**
 * User: ykrasik
 * Date: 08/01/14
 */
public class JerminalLibGdxExample extends ApplicationAdapter {
    private LibGdxConsole console;

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
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final int maxBufferEntries = 30;
        final LibGdxConsoleWidgetFactory widgetFactory = new JerminalLibGdxTestConsoleWidgetFactory();

        final LibGdxConsoleBuilder builder = new LibGdxConsoleBuilder(width, height, maxBufferEntries, widgetFactory);
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
                    public ExecutorReturnValue execute(CommandArgs args, ExecuteContext context) {
                        final int integer = args.getInt("mandatoryInt");
                        final boolean bool = args.getBool("optionalBool");
                        context.println("yay: int = %d, bool = %s", integer, bool);
                        return success();
                    }
                })
                .build(),
            new ShellCommandBuilder("test")
              .setDescription("test Command")
              .addParam(
                  new StringParamBuilder("stringy")
                    .setConstantPossibleValues("test1", "value2", "param3")
                    .build()
              )
              .addParam(
                  new BooleanParamBuilder("booleany")
                    .build()
              )
              .setExecutor(new CommandExecutor() {
                  @Override
                  public ExecutorReturnValue execute(CommandArgs args, ExecuteContext context) {
                      final String str = args.getString("stringy");
                      final boolean bool = args.getBool("booleany");
                      context.println("yay: string = %s, bool = %s", str, bool);
                      return success();
                  }
              })
              .build()
        );
    }

    @Override
    public void resize(int width, int height) {
        console.resize(width, height);
    }

    @Override
    public void render() {
        console.draw();
        console.act(Gdx.graphics.getDeltaTime());
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

package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.rawcod.jerminal.Shell;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecuteContext;
import com.rawcod.jerminal.command.parameters.bool.BooleanParamBuilder;
import com.rawcod.jerminal.command.parameters.number.IntegerParamBuilder;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.output.terminal.Terminal;
import com.rawcod.jerminal.output.terminal.TerminalOutputHandler;
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
        final ShellFileSystem fileSystem = createFileSystem();

        this.shell = new Shell(new TerminalOutputHandler(terminal), fileSystem, maxCommandHistory);

        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final int toggleKey = Keys.GRAVE;
        final int maxBufferEntries = 30;
        final int maxCommandHistory = 20;
        final LibGdxConsoleWidgetFactory widgetFactory = new JerminalLibGdxTestConsoleWidgetFactory();
        final Terminal terminal = new LibGdxTerminal(width, height, maxBufferEntries, widgetFactory, this);
        console = new LibGdxConsole(width, height, toggleKey, maxBufferEntries, maxCommandHistory, fileSystem, widgetFactory);

        Gdx.input.setInputProcessor(console);
        console.activate();
    }

    private ShellFileSystem createFileSystem() {
        final ShellFileSystem fileSystem = new ShellFileSystem(root, globalCommands);

        fileSystem.add("nested/d/1possible");
        fileSystem.add("nested/d/2possible");
        fileSystem.add("nested/dir/singlePossible");
        fileSystem.add("nested/dir1/singlePossible");
        fileSystem.add("nested/dir2/singlePossible");
        fileSystem.add("nested/directory/singlePossible/multiplePossible1/singlePossible");
        fileSystem.add("nested/directory/singlePossible/multiplePossible2/singlePossible");

        fileSystem.add(
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
                .build()
        );

        return fileSystem;
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

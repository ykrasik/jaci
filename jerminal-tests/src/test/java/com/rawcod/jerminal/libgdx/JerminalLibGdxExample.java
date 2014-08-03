package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.parameters.bool.OptionalBooleanParam;
import com.rawcod.jerminal.command.parameters.number.IntegerParam;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandImpl;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectoryImpl;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandArgs;
import com.rawcod.jerminal.command.parameters.CommandParam;

import java.util.Set;

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
        final ShellManager manager = createManager();

        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final int toggleKey = Keys.GRAVE;
        final int maxBufferEntries = 30;
        final int maxCommandHistory = 20;
        final LibGdxConsoleWidgetFactory widgetFactory = new JerminalLibGdxTestConsoleWidgetFactory();
        console = new LibGdxConsole(width, height, toggleKey, maxBufferEntries, maxCommandHistory, manager, widgetFactory);

        Gdx.input.setInputProcessor(console);
        console.activate();
    }

    private ShellManager createManager() {
        final ShellManager manager = new ShellManager();

        manager.addEntry(
            new ShellDirectoryImpl("nested").addEntries(
                new ShellDirectoryImpl("d").addEntries(
                    new ShellDirectoryImpl("1possible"),
                    new ShellDirectoryImpl("2possible")
                ),
                new ShellDirectoryImpl("dir").addEntry(
                    new ShellDirectoryImpl("singlePossible")
                ),
                new ShellDirectoryImpl("dir1").addEntry(
                    new ShellDirectoryImpl("singlePossible")
                ),
                new ShellDirectoryImpl("dir2").addEntry(
                    new ShellDirectoryImpl("singlePossible")
                ),
                new ShellDirectoryImpl("directory").addEntries(
                    new ShellDirectoryImpl("singlePossible").addEntries(
                        new ShellDirectoryImpl("multiplePossible1").addEntry(
                            new ShellDirectoryImpl("singlePossible")
                        ),
                        new ShellDirectoryImpl("multiplePossible2").addEntry(
                            new ShellDirectoryImpl("singlePossible")
                        )
                    )
                )
            )
        );

        manager.addEntries(
            new ShellCommandImpl(
                "cmd",
                "cmd",
                new CommandParam[] {
                    new IntegerParam("mandatoryInt"),
                    new OptionalBooleanParam("optionalBool", false, defaultValueSupplier)
                },
                new CommandExecutor() {
                    @Override
                    protected ExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
                        final int integer = args.popInt();
                        final boolean bool = args.popBool();
                        return success("yay: int = %d, bool = %s", integer, bool);
                    }
                }
            )
        );
        
        return manager;
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

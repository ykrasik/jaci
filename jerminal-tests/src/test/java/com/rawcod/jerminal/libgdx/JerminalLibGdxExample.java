package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.command.ShellCommand;
import com.rawcod.jerminal.shell.entry.command.ShellCommandArgs;
import com.rawcod.jerminal.shell.entry.command.ShellCommandExecutor;
import com.rawcod.jerminal.shell.entry.directory.ShellDirectory;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.entry.parameters.bool.OptionalBoolShellParam;
import com.rawcod.jerminal.shell.entry.parameters.integer.IntShellParam;
import com.rawcod.jerminal.shell.returnvalue.ShellExecuteReturnValue;

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
            new ShellDirectory("nested").addEntries(
                new ShellDirectory("d").addEntries(
                    new ShellDirectory("1possible"),
                    new ShellDirectory("2possible")
                ),
                new ShellDirectory("dir").addEntry(
                    new ShellDirectory("singlePossible")
                ),
                new ShellDirectory("dir1").addEntry(
                    new ShellDirectory("singlePossible")
                ),
                new ShellDirectory("dir2").addEntry(
                    new ShellDirectory("singlePossible")
                ),
                new ShellDirectory("directory").addEntries(
                    new ShellDirectory("singlePossible").addEntries(
                        new ShellDirectory("multiplePossible1").addEntry(
                            new ShellDirectory("singlePossible")
                        ),
                        new ShellDirectory("multiplePossible2").addEntry(
                            new ShellDirectory("singlePossible")
                        )
                    )
                )
            )
        );

        manager.addEntries(
            new ShellCommand(
                "cmd",
                "cmd",
                new ShellParam[] {
                    new IntShellParam("mandatoryInt"),
                    new OptionalBoolShellParam("optionalBool", false)
                },
                new ShellCommandExecutor() {
                    @Override
                    protected ShellExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
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

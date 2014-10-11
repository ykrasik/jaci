package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.ShellImpl;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.numeric.IntegerParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.string.StringParamBuilder;
import com.github.ykrasik.jerminal.api.commandline.CommandLineDriver;
import com.github.ykrasik.jerminal.api.commandline.ShellWithCommandLine;
import com.github.ykrasik.jerminal.api.commandline.ShellWithCommandLineImpl;
import com.github.ykrasik.jerminal.api.display.terminal.Terminal;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalDisplayDriver;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
public class JerminalJavaFxExample extends Application {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Scene scene;

    private ShellWithCommandLine shell;

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
        final Terminal terminal = new JavaFxTerminal(textArea);

        final ShellFileSystem fileSystem = createFileSystem();

        final Shell shellImpl = new ShellImpl(fileSystem, new TerminalDisplayDriver(terminal));

        final TextField textField = findById("textField", TextField.class);
        final JavaFxCommandLineDriver commandLineDriver = new JavaFxCommandLineDriver(textField);

        this.shell = new ShellWithCommandLineImpl(shellImpl, commandLineDriver);

        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case ENTER: shell.execute(); break;
                    case TAB: shell.assist(); break;
                    case UP: shell.setPrevCommandLineFromHistory(); break;
                    case DOWN: shell.setNextCommandLineFromHistory(); break;
                }
            }
        });
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

    private static class JavaFxCommandLineDriver implements CommandLineDriver {
        private final TextField textField;

        private JavaFxCommandLineDriver(TextField textField) {
            this.textField = Objects.requireNonNull(textField);
        }

        @Override
        public String read() {
            return textField.getText();
        }

        @Override
        public String readUntilCaret() {
            final int caretPosition = textField.getCaretPosition();
            return textField.getText(0, caretPosition);
        }

        @Override
        public void set(String commandLine) {
            textField.setText(commandLine);
        }

        @Override
        public void clear() {
            textField.clear();
        }
    }
}

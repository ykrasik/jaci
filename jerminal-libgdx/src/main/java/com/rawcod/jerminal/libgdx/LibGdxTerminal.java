package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rawcod.jerminal.Terminal;
import com.rawcod.jerminal.shell.entry.directory.ShellTree;

import java.util.List;

/**
 * User: yevgenyk
 * Date: 07/01/14
 */
public class LibGdxTerminal extends Stage implements Terminal {
    private final Table terminalScreen;

    private final LibGdxTerminalBuffer buffer;
    private final ScrollPane bufferScrollPane;

    private final Label currentPath;

    private final TextField textField;

    private boolean needsFocus;

    public LibGdxTerminal(float width,
                          float height,
                          int maxBufferEntries,
                          LibGdxConsoleWidgetFactory widgetFactory,
                          final LibGdxConsole console) {
        super(width, height, true);

        // The buffer will show all the messages we got.
        buffer = createBuffer(maxBufferEntries, widgetFactory);

        // Wrap the buffer in a scrollPane to get a nice scrollbar.
        bufferScrollPane = createBufferScrollPane(buffer);

        // A "current-path" thing
        currentPath = widgetFactory.createCurrentPathLabel("$");
        final Table currentPathTable = new Table();
        currentPathTable.add(currentPath).fill().padLeft(3).padRight(5);
        currentPathTable.setBackground(widgetFactory.createCurrentPathLabelBackground());
        currentPathTable.debug();

        // TextField to input commands.
        textField = createInputTextField(widgetFactory);

        // A close console button
        final Button closeButton = widgetFactory.createCloseButton();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                console.deactivate();
            }
        });

        // The table that will contain the bottom row - current path, text input, close button
        final Table bottomRow = new Table();
        bottomRow.setBackground(widgetFactory.createConsoleBottomRowBackground());
        bottomRow.add(currentPathTable).fill();
        bottomRow.add(textField).fill().expandX();
        bottomRow.add(closeButton).fill().width(closeButton.getWidth());
        bottomRow.debug();

        terminalScreen = new Table();
        terminalScreen.setBackground(widgetFactory.createTerminalBufferBackground());
        terminalScreen.pad(0);
        terminalScreen.add(bufferScrollPane).fill().expand();
        terminalScreen.row();
        terminalScreen.add(bottomRow).fill();

        terminalScreen.top().left();
        terminalScreen.setFillParent(true);
        terminalScreen.setVisible(false);

        terminalScreen.debug();

        this.addActor(terminalScreen);
    }

    private LibGdxTerminalBuffer createBuffer(int maxBufferEntries, LibGdxConsoleWidgetFactory widgetFactory) {
        final LibGdxTerminalBuffer buffer = new LibGdxTerminalBuffer(maxBufferEntries, widgetFactory);
        buffer.bottom().left();
        buffer.debug();
        return buffer;
    }

    private ScrollPane createBufferScrollPane(LibGdxTerminalBuffer buffer) {
        final ScrollPane scrollPane = new ScrollPane(buffer);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setupOverscroll(0, 0, 0);
        scrollPane.layout();
        scrollPane.setScrollPercentY(1);
        return scrollPane;
    }

    private TextField createInputTextField(LibGdxConsoleWidgetFactory widgetFactory) {
        return widgetFactory.createInputTextField("");
    }

    @Override
    public void displayMessage(String message) {
        final String displayMessage = String.format("[%s] %s", currentPath.getText(), message);
        print(displayMessage, Color.WHITE);
    }

    @Override
    public void displayCommandReturnMessage(String message) {
        print(message, Color.WHITE);
    }

    @Override
    public void displayError(String error) {
        final String errorMessage = "Error: " + error;
        print(errorMessage, Color.PINK);
    }

    @Override
    public void displayUsage(String usage) {
        final String usageMessage = "Usage: " + usage;
        print(usageMessage, Color.CYAN);
    }

    @Override
    public void displaySuggestions(List<String> suggestions) {
        final String suggestionMessage = "Suggestions: " + suggestions;
        print(suggestionMessage, Color.ORANGE);
    }

    @Override
    public void displayShellTree(ShellTree shellTree) {
        final StringBuilder sb = new StringBuilder();
        serializeShellTree(sb, shellTree, 0);
        print(sb.toString(), Color.WHITE);
    }

    private void serializeShellTree(StringBuilder sb, ShellTree shellTree, int depth) {
        // Print root
        if (shellTree.isDirectory()) {
            sb.append('[');
        }
        sb.append(shellTree.getName());
        if (shellTree.isDirectory()) {
            sb.append(']');
        }
        if (!shellTree.isDirectory()) {
            sb.append(" : ");
            sb.append(shellTree.getDescription());
        }
        sb.append('\n');

        // Print children
        if (shellTree.isDirectory()) {
            for (ShellTree child : shellTree.getChildren()) {
                sb.append('|');
                appendDepthSpaces(sb, depth + 1);
                serializeShellTree(sb, child, depth + 1);
            }
        }
    }

    private void appendDepthSpaces(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
    }

    private void print(String message, Color color) {
        buffer.println(message, color);
        bufferScrollPane.layout();
        bufferScrollPane.setScrollPercentY(1);
        bufferScrollPane.updateVisualScroll();
    }

    @Override
    public void setCommandLine(String commandLine) {
        textField.setText(commandLine);
    }

    @Override
    public void setCommandLineCursor(int index) {
        textField.setCursorPosition(index);
    }

    @Override
    public void setCurrentPath(List<String> path) {
        final StringBuffer sb = new StringBuffer();
        for (String entry : path) {
            sb.append(entry);
            sb.append('/');
        }
        if (sb.length() != 0) {
            sb.replace(sb.length() - 1, sb.length(), " ");
        }
        sb.append('$');
        currentPath.setText(sb.toString());
    }

    public String readCommandLine() {
        return textField.getText();
    }

    public String readCommandLineUntilCursor() {
        return textField.getText().substring(0, textField.getCursorPosition());
    }

    public void activate() {
        terminalScreen.setVisible(true);

        // Delegate giving the textField keyboard focus so that the key that activated
        // the terminal doesn't get typed into the textField.
        needsFocus = true;
    }

    public void deactivate() {
        terminalScreen.setVisible(false);

        // Clear stage keyboard focus
        this.setKeyboardFocus(null);
    }

    @Override
    public void draw() {
        if (needsFocus) {
            this.setKeyboardFocus(textField);
            needsFocus = false;
        }
        super.draw();
    }
}

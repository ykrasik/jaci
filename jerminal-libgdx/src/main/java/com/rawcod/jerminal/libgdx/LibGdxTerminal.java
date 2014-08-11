package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rawcod.jerminal.output.terminal.Terminal;

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

    private ConsoleActivationListener listener;

    public LibGdxTerminal(float width,
                          float height,
                          int maxBufferEntries,
                          LibGdxConsoleWidgetFactory widgetFactory) {
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
                deactivate();
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
    public void clearCommandLine() {
        setCommandLine("");
    }

    @Override
    public void setCommandLine(String commandLine) {
        textField.setText(commandLine);
        textField.setCursorPosition(commandLine.length());
    }

    @Override
    public void print(String message) {
        print(message, Color.WHITE);
    }

    @Override
    public void printError(String message) {
        print(message, Color.PINK);
    }

    private void print(String message, Color color) {
        buffer.println(message, color);
        bufferScrollPane.layout();
        bufferScrollPane.setScrollPercentY(1);
        bufferScrollPane.updateVisualScroll();
    }

    public void setListener(ConsoleActivationListener listener) {
        this.listener = listener;
    }

//    @Override
//    public void setCurrentPath(List<String> path) {
//        final StringBuffer sb = new StringBuffer();
//        for (String entry : path) {
//            sb.append(entry);
//            sb.append('/');
//        }
//        if (sb.length() != 0) {
//            sb.replace(sb.length() - 1, sb.length(), " ");
//        }
//        sb.append('$');
//        currentPath.setText(sb.toString());
//    }

    public String readCommandLine() {
        return textField.getText();
    }

    public String readCommandLineUntilCursor() {
        return textField.getText().substring(0, textField.getCursorPosition());
    }

    public boolean isActive() {
        return terminalScreen.isVisible();
    }

    public void toggle() {
        if (!isActive()) {
            activate();
        } else {
            deactivate();
        }
    }

    public void activate() {
        if (isActive()) {
            return;
        }

        terminalScreen.setVisible(true);

        // Delegate giving the textField keyboard focus so that the key that activated
        // the terminal doesn't get typed into the textField.
        needsFocus = true;

        // Notify listener
        if (listener != null) {
            listener.activated();
        }
    }

    public void deactivate() {
        if (!isActive()) {
            return;
        }

        terminalScreen.setVisible(false);

        // Clear stage keyboard focus
        this.setKeyboardFocus(null);

        // Notify listener
        if (listener != null) {
            listener.deactivated();
        }
    }

    @Override
    public void draw() {
        if (!isActive()) {
            return;
        }

        if (needsFocus) {
            this.setKeyboardFocus(textField);
            needsFocus = false;
        }

        super.draw();
    }

    @Override
    public void act(float delta) {
        if (!isActive()) {
            return;
        }

        super.act(delta);
    }
}

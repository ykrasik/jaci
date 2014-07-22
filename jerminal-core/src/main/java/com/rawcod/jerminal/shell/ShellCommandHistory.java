package com.rawcod.jerminal.shell;

import com.google.common.base.Optional;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * User: ykrasik
 * Date: 07/01/14
 */
public class ShellCommandHistory {
    private final Deque<String> prevCommands;
    private final Deque<String> nextCommands;

    private final int maxCommandsHistory;

    public ShellCommandHistory(int maxCommandsHistory) {
        this.maxCommandsHistory = maxCommandsHistory;
        this.prevCommands = new ArrayDeque<>(maxCommandsHistory);
        this.nextCommands = new ArrayDeque<>(maxCommandsHistory);
    }

    public Optional<String> getPrevCommand() {
        if (prevCommands.isEmpty()) {
            return null;
        }
        if (prevCommands.size() == 1) {
            return prevCommands.peek();
        }

        final String prevCommand = prevCommands.pollLast();
        nextCommands.addFirst(prevCommand);
        return prevCommand;
    }

    public Optional<String>  getNextCommand() {
        if (nextCommands.isEmpty()) {
            return null;
        }
        if (nextCommands.size() == 1){
            return nextCommands.peek();
        }

        final String nextCommand = nextCommands.pollFirst();
        prevCommands.addLast(nextCommand);
        return nextCommand;
    }

    public void pushCommand(String command) {
        returnNextCommands();
        if (prevCommands.size() >= maxCommandsHistory) {
            prevCommands.removeFirst();
        }
        prevCommands.addLast(command);
    }

    private void returnNextCommands() {
        // Move all commands currently in nextCommands to prevCommands.
        // This is done if a command is pushed while navigating the command history.
        prevCommands.addAll(nextCommands);
        nextCommands.clear();
    }
}

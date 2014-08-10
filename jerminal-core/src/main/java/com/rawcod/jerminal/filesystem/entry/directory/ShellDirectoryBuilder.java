package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
* User: ykrasik
* Date: 10/08/2014
* Time: 23:09
*/
public class ShellDirectoryBuilder {
    private final ShellDirectoryBuilder parentBuilder;
    private final String name;
    private final String description;
    private final Map<String, ShellDirectoryBuilder> childDirectoryBuilders;
    private final Map<String, ShellCommand> childCommands;

    public ShellDirectoryBuilder(String name, String description) {
        this(null, name, description);
    }

    private ShellDirectoryBuilder(ShellDirectoryBuilder parentBuilder, String name, String description) {
        this.parentBuilder = parentBuilder;
        this.name = name;
        this.description = description;
        this.childDirectoryBuilders = new HashMap<>();
        this.childCommands = new HashMap<>();
    }

    public ShellDirectory build() {
        final Map<String, ShellDirectory> childDirectories = new HashMap<>(childDirectoryBuilders.size());
        for (Entry<String, ShellDirectoryBuilder> entry : childDirectoryBuilders.entrySet()) {
            final ShellDirectory childDirectory = entry.getValue().build();
            childDirectories.put(entry.getKey(), childDirectory);
        }
        final ShellDirectoryImpl directory = new ShellDirectoryImpl(name, description, childDirectories, childCommands);
        for (ShellDirectory childDirectory : childDirectories.values()) {
            // This downcast isn't great, but it's guaranteed to always succeed.
            // FIXME: Figure out how to get rid of the need for a parent.
            ((ShellDirectoryImpl) childDirectory).setParent(directory);
        }
        return directory;
    }

    public ShellDirectoryBuilder getOrCreateDirectory(String name, String description) {
        // If such a child directory already exists, return it.
        final ShellDirectoryBuilder childDirectory = childDirectoryBuilders.get(name);
        if (childDirectory != null) {
            return childDirectory;
        }

        // Assert 'name' is legal and isn't already taken by a child command.
        assertLegalName(name);
        assertNoCommand(name);

        // Create a new child directory, link it and return.
        final ShellDirectoryBuilder builder = new ShellDirectoryBuilder(this, name, description);
        childDirectoryBuilders.put(name, builder);
        return builder;
    }

    public void addCommands(ShellCommand... commands) {
        for (ShellCommand command : commands) {
            addCommand(command);
        }
    }

    public void addCommand(ShellCommand command) {
        final String commandName = command.getName();

        if (ShellDirectory.THIS.equals(name) || ShellDirectory.PARENT.equals(name)) {
            throw new ShellException("Illegal name for directory: " + name);
        }

        // Assert 'commandName' is legal and isn't already taken by a child directory or command.
        assertLegalName(commandName);
        assertNoDirectory(commandName);
        assertNoCommand(commandName);

        // Link the command.
        childCommands.put(commandName, command);
    }

    private void assertLegalName(String name) {
        if (ShellDirectory.THIS.equals(name) || ShellDirectory.PARENT.equals(name)) {
            throw new ShellException("Illegal name for entry: '%s'", name);
        }
    }

    private void assertNoDirectory(String name) {
        if (childDirectoryBuilders.containsKey(name)) {
            throw new ShellException("Directory already contains a child directory with name '%s'!", name);
        }
    }

    private void assertNoCommand(String name) {
        if (childCommands.containsKey(name)) {
            throw new ShellException("Directory already contains a child command with name '%s'!", name);
        }
    }
}

package com.rawcod.jerminal.filesystem;

import com.google.common.base.Splitter;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectoryBuilder;

import java.util.*;

/**
* User: ykrasik
* Date: 10/08/2014
* Time: 23:18
*/
public class ShellFileSystemBuilder {
    private static final char PATH_DELIMITER = '/';
    private static final String PATH_DELIMITER_STR = String.valueOf(PATH_DELIMITER);
    private static final char DESCRIPTION_DELIMITER = ':';

    private static final Splitter PATH_SPLITTER = Splitter.on(PATH_DELIMITER).trimResults();
    private static final Splitter DESCRIPTION_SPLITTER = Splitter.on(DESCRIPTION_DELIMITER).trimResults();

    private final ShellDirectoryBuilder rootBuilder;
    private final Map<String, ShellCommand> globalCommands;

    public ShellFileSystemBuilder() {
        this.rootBuilder = new ShellDirectoryBuilder("root", "root");
        this.globalCommands = new HashMap<>();
    }

    public ShellFileSystem build() {
        final ShellDirectory root = rootBuilder.build();
        return new ShellFileSystem(root, globalCommands);
    }

    public ShellFileSystemBuilder add(ShellCommand... commands) {
        return add("", commands);
    }

    public ShellFileSystemBuilder add(String path, ShellCommand... commands) {
        final ShellDirectoryBuilder directory = getOrCreatePath(path);
        directory.addCommands(commands);
        return this;
    }

    public ShellFileSystemBuilder addGlobalCommand(ShellCommand... globalCommands) {
        return addGlobalCommand(Arrays.asList(globalCommands));
    }

    public ShellFileSystemBuilder addGlobalCommand(Collection<ShellCommand> globalCommands) {
        for (ShellCommand globalCommand : globalCommands) {
            final String name = globalCommand.getName();
            if (this.globalCommands.containsKey(name)) {
                throw new ShellException("Already have a global command with name: '%s'", name);
            }
            this.globalCommands.put(name, globalCommand);
        }
        return this;
    }

    private ShellDirectoryBuilder getOrCreatePath(String path) {
        // Ignore any leading '/', paths always start from root.
        final String trimmedPath = path.trim();
        final String pathToSplit = trimmedPath.startsWith(PATH_DELIMITER_STR) ? trimmedPath.substring(1) : trimmedPath;
        final List<String> splitPath = PATH_SPLITTER.splitToList(pathToSplit);

        // Advance along the path, creating missing child directories as required.
        ShellDirectoryBuilder currentDir = rootBuilder;
        for (int i = 0; i < splitPath.size(); i++) {
            final String pathElement = splitPath.get(i);
            if (pathElement.isEmpty()) {
                if (i == splitPath.size() - 1) {
                    // The last element of the path is allowed to be empty.
                    // This allows any path to end with a '/', and it will simply ignore the last '/'.
                    // For example: "path/to/element/" is the same as "path/to/element".
                    continue;
                } else {
                    throw new ShellException("Path elements aren't allowed to be empty!");
                }
            }

            currentDir = parsePathElement(currentDir, pathElement);
        }
        return currentDir;
    }

    private ShellDirectoryBuilder parsePathElement(ShellDirectoryBuilder currentDir, String pathElement) {
        // A pathElement is allowed to contain an optional description for the directory.
        // The format is "{name}[:{description}]
        final List<String> splitPathElement = DESCRIPTION_SPLITTER.splitToList(pathElement);

        final String name = splitPathElement.get(0);
        final String description = splitPathElement.size() == 2 ? splitPathElement.get(1) : "directory";
        return currentDir.getOrCreateDirectory(name, description);
    }
}

package com.rawcod.jerminal.filesystem;

import com.google.common.base.Splitter;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectoryImpl;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.*;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:23
 */
public class ShellFileSystem {
    private static final char PATH_DELIMITER = '/';
    private static final String PATH_DELIMITER_STR = String.valueOf(PATH_DELIMITER);
    private static final char DESCRIPTION_DELIMITER = ':';

    private static final Splitter PATH_SPLITTER = Splitter.on(PATH_DELIMITER).trimResults();
    private static final Splitter DESCRIPTION_SPLITTER = Splitter.on(DESCRIPTION_DELIMITER).trimResults();

    private final ShellDirectory root;
    private final Set<ShellCommand> globalCommands;

    public ShellFileSystem() {
        this.root = new ShellDirectoryImpl("root", "root", null);
        this.globalCommands = new HashSet<>();
    }

    ShellDirectory getRoot() {
        return root;
    }

    Set<ShellCommand> getGlobalCommands() {
        return Collections.unmodifiableSet(globalCommands);
    }

    public ShellFileSystem add(ShellCommand... commands) {
        return add("", commands);
    }

    public ShellFileSystem add(String path, ShellCommand... commands) {
        final ShellDirectory directory = getOrCreatePath(path);
        directory.addCommands(commands);
        return this;
    }

    public ShellFileSystem addGlobalCommand(ShellCommand... globalCommands) {
        return addGlobalCommand(Arrays.asList(globalCommands));
    }

    public ShellFileSystem addGlobalCommand(Collection<ShellCommand> globalCommands) {
        this.globalCommands.addAll(globalCommands);
        return this;
    }

    private ShellDirectory getOrCreatePath(String path) {
        // Ignore any leading '/', paths always start from root.
        final String trimmedPath = path.trim();
        final String pathToSplit = trimmedPath.startsWith(PATH_DELIMITER_STR) ? trimmedPath.substring(1) : trimmedPath;
        final List<String> splitPath = PATH_SPLITTER.splitToList(pathToSplit);

        // Advance along the path, creating missing child directories as required.
        ShellDirectory currentDir = root;
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

    private ShellDirectory parsePathElement(ShellDirectory currentDir, String pathElement) {
        // A pathElement is allowed to contain an optional description for the directory.
        // The format is "{name}[:{description}]
        final List<String> splitPathElement = DESCRIPTION_SPLITTER.splitToList(pathElement);

        final String name = splitPathElement.get(0);
        final ParseEntryReturnValue returnValue = currentDir.parseDirectory(name);
        if (returnValue.isSuccess()) {
            // Directory already contains such a child directory.
            return returnValue.getSuccess().getEntry().getAsDirectory();
        }

        // Directory does not contain such a child directory, create it.
        final String description = splitPathElement.size() == 2 ? splitPathElement.get(1) : "directory";
        return currentDir.createChildDirectory(name, description);
    }
}

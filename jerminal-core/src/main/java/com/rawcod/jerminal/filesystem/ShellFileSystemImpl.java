package com.rawcod.jerminal.filesystem;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:37
 */
public class ShellFileSystemImpl implements ShellFileSystem {
    private static final char DELIMITER = '/';
    private static final String DELIMITER_STR = String.valueOf(DELIMITER);
    private static final String THIS = ".";
    private static final String PARENT = "..";
    private static final Splitter SPLITTER = Splitter.on(DELIMITER);
    private static final List<String> ILLEGAL_NAMES = Arrays.asList(DELIMITER_STR, THIS, PARENT);

    private final ShellDirectory root;
    private final Trie<ShellCommand> globalCommands;

    private ShellDirectory currentDirectory;

    ShellFileSystemImpl(ShellDirectory root,
                        Trie<ShellCommand> globalCommands) {
        this.root = root;
        this.globalCommands = globalCommands;
        this.currentDirectory = root;
    }

    @Override
    public ShellDirectory getRoot() {
        return root;
    }

    @Override
    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public void setCurrentDirectory(ShellDirectory directory) {
        this.currentDirectory = checkNotNull(directory, "directory");
    }

    @Override
    public ShellCommand parsePathToCommand(String rawPath) throws ParseException {
        // If rawPath does not contain a single delimiter, we can try use it as the command name.
        final int delimiterIndex = rawPath.lastIndexOf(DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath does not contain a delimiter.
            // It could either be a global command, or a command from the currentDirectory.
            final Optional<ShellCommand> globalCommand = globalCommands.get(rawPath);
            if (globalCommand.isPresent()) {
                return globalCommand.get();
            } else {
                return currentDirectory.parseCommand(rawPath);
            }
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory parse the last entry as a command.
        // So in "path/to/command", parse "path/to" as path to directory "to", and let "to" parse "command".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex);
        final String rawCommand = rawPath.substring(delimiterIndex + 1);
        final ShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);
        return lastDirectory.parseCommand(rawCommand);
    }

    @Override
    public ShellDirectory parsePathToDirectory(String rawPath) throws ParseException {
        // Remove leading and trailing '/'.
        final boolean startsFromRoot = rawPath.startsWith(DELIMITER_STR);
        final String pathToSplit = StringUtils.removeLeadingAndTrailing(rawPath, DELIMITER_STR);

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // Parse all pathElements as directories.
        ShellDirectory dir = startsFromRoot ? root : currentDirectory;
        for (String pathElement : splitPath) {
            if (THIS.equals(pathElement)) {
                continue;
            }
            if (PARENT.equals(pathElement)) {
                final ShellDirectory parent = dir.getParent();
                if (parent != null) {
                    dir = parent;
                } else {
                    throw ParseErrors.directoryDoesNotHaveParent(dir.getName());
                }
            }

            dir = dir.parseDirectory(pathElement);
        }

        return dir;
    }

    @Override
    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter, just autoComplete it from the current directory.
            final Trie<AutoCompleteType> possibilities = currentDirectory.autoCompleteDirectory(rawPath);
            return new AutoCompleteReturnValue(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry as a directory.
        // So in "path/to/directory", parse "path/to" as path to directory "to", and let "to" autoComplete "directory".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex);
        final String rawEntry = rawPath.substring(delimiterIndex + 1);
        final ShellDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);
        final Trie<AutoCompleteType> possibilities = lastDirectory.autoCompleteDirectory(rawEntry);
        return new AutoCompleteReturnValue(rawEntry, possibilities);
    }

    @Override
    public AutoCompleteReturnValue autoCompletePath(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(DELIMITER);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter.
            // It could be an entry from the current directory or a global command.
            final Trie<AutoCompleteType> entryPossibilities = currentDirectory.autoCompleteEntry(rawPath);
            final Trie<AutoCompleteType> globalCommandPossibilities = this.globalCommands.subTrie(rawPath).map(AutoCompleteMappers.commandMapper());
            final Trie<AutoCompleteType> possibilities = entryPossibilities.union(globalCommandPossibilities);
            return new AutoCompleteReturnValue(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry.
        // So in "path/to/entry", parse "path/to" as path to directory "to", and let "to" autoComplete "entry".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex);
        final String rawEntry = rawPath.substring(delimiterIndex + 1);
        final ShellDirectory lastDirectory;
        if (pathToLastDirectory.isEmpty()) {
            // This can only happen if the commandLine started with '/'.
            lastDirectory = root;
        } else {
            lastDirectory = parsePathToDirectory(pathToLastDirectory);
        }

        final Trie<AutoCompleteType> possibilities = lastDirectory.autoCompleteEntry(rawEntry);
        return new AutoCompleteReturnValue(rawEntry, possibilities);
    }

    public static boolean isLegalName(String name) {
        return !ILLEGAL_NAMES.contains(name);
    }
}

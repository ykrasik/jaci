package com.rawcod.jerminal.filesystem;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:37
 */
public class ShellFileSystem {
    private static final char DELIMITER = '/';
    private static final String DELIMITER_STR = String.valueOf(DELIMITER);
    private static final String THIS = ".";
    private static final String PARENT = "..";
    private static final Splitter SPLITTER = Splitter.on(DELIMITER);
    private static final List<String> ILLEGAL_NAMES = Arrays.asList(DELIMITER_STR, THIS, PARENT);

    private final ShellDirectory root;
    private final Trie<ShellCommand> globalCommands;
    private final CurrentDirectoryContainer currentDirectoryContainer;

    ShellFileSystem(ShellDirectory root,
                    Trie<ShellCommand> globalCommands,
                    CurrentDirectoryContainer currentDirectoryContainer) {
        this.root = root;
        this.globalCommands = globalCommands;
        this.currentDirectoryContainer = currentDirectoryContainer;
        currentDirectoryContainer.setCurrentDirectory(root);
    }

    public ShellDirectory getRoot() {
        return root;
    }

    public ParseEntryReturnValue parsePathToCommand(String rawPath) {
        return doParsePath(rawPath, false);
    }

    public ParseEntryReturnValue parsePathToDirectory(String rawPath) {
        return doParsePath(rawPath, true);
    }

    private ParseEntryReturnValue doParsePath(String rawPath, boolean directory) {
        // Remove leading and trailing '/'.
        final boolean startsFromRoot = rawPath.startsWith(DELIMITER_STR);
        final String pathToSplit = StringUtils.removeLeadingAndTrailing(rawPath, DELIMITER_STR);

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // First check if rawPath is a global commands.
        // It may only be a global command if splitPath only has 1 entry that doesn't start from root.
        if (!directory && !startsFromRoot && splitPath.size() == 1) {
            final String rawEntry = splitPath.get(0);
            final Optional<ShellCommand> globalCommand = globalCommands.get(rawEntry);
            if (globalCommand.isPresent()) {
                return ParseEntryReturnValue.success(globalCommand.get());
            }
        }

        // If the path consists of N entries, parse N-1 entries as directories
        // and parse the last entry according to the filter.
        final List<String> directoryPath = splitPath.subList(0, splitPath.size() - 1);
        final ParseEntryReturnValue getLastDirReturnValue = getLastDirectory(directoryPath, startsFromRoot);
        if (getLastDirReturnValue.isFailure()) {
            return getLastDirReturnValue;
        }

        final ShellDirectory lastDir = getLastDirReturnValue.getSuccess().getEntry().getAsDirectory();

        // Parse the last entry in the path according to the filter.
        final String rawEntry = splitPath.get(splitPath.size() - 1);
        final ParseEntryReturnValue returnValue;
        if (directory) {
            if (THIS.equals(rawEntry)) {
                return ParseEntryReturnValue.success(lastDir);
            }
            if (PARENT.equals(rawEntry)) {
                final ShellDirectory parent = lastDir.getParent();
                if (parent != null) {
                    return ParseEntryReturnValue.success(parent);
                } else {
                    return ParseErrors.directoryDoesNotHaveParent(lastDir.getName());
                }
            }
            returnValue = lastDir.parseDirectory(rawEntry);
        } else {
            returnValue = lastDir.parseCommand(rawEntry);
        }
        if (returnValue.isFailure()) {
            return ParseEntryReturnValue.failure(returnValue.getFailure());
        }

        final ShellEntry entry = returnValue.getSuccess().getEntry();
        return ParseEntryReturnValue.success(entry);
    }

    private ParseEntryReturnValue getLastDirectory(List<String> path, boolean startsFromRoot) {
        ShellDirectory dir = startsFromRoot ? root : currentDirectoryContainer.getCurrentDirectory();
        for (String entry : path) {
            if (THIS.equals(entry)) {
                continue;
            }
            if (PARENT.equals(entry)) {
                final ShellDirectory parent = dir.getParent();
                if (parent != null) {
                    dir = parent;
                } else {
                    return ParseErrors.directoryDoesNotHaveParent(dir.getName());
                }
            }

            final ParseEntryReturnValue returnValue = dir.parseDirectory(entry);
            if (returnValue.isFailure()) {
                // Invalid directory along the path.
                return returnValue;
            }

            dir = returnValue.getSuccess().getEntry().getAsDirectory();
        }

        return ParseEntryReturnValue.success(dir);
    }

    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) {
        return doAutoCompletePath(rawPath, true);
    }

    public AutoCompleteReturnValue autoCompletePath(String rawPath) {
        return doAutoCompletePath(rawPath, false);
    }

    private AutoCompleteReturnValue doAutoCompletePath(String rawPath, boolean directory) {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int lastIndexOfDelimiter = rawPath.lastIndexOf(DELIMITER);
        final String pathToParse = rawPath.substring(0, lastIndexOfDelimiter != - 1 ? lastIndexOfDelimiter : 0);
        final String autoCompleteArg = rawPath.substring(lastIndexOfDelimiter + 1);

        final ShellDirectory lastDir;
        if (pathToParse.isEmpty()) {
            lastDir = currentDirectoryContainer.getCurrentDirectory();
        } else {
            final ParseEntryReturnValue returnValue = parsePathToDirectory(pathToParse);
            if (returnValue.isFailure()) {
                return AutoCompleteErrors.parseError(returnValue.getFailure());
            }
            lastDir = returnValue.getSuccess().getEntry().getAsDirectory();
        }

        // AutoComplete the last entry along the path.
        final AutoCompleteReturnValue entryReturnValue;
        if (directory) {
            entryReturnValue = lastDir.autoCompleteDirectory(autoCompleteArg);
        } else {
            entryReturnValue = lastDir.autoCompleteEntry(autoCompleteArg);
        }

        // If pathToParse was empty and the original path doesn't start with a delimiter,
        // autoCompleteArg could be a global command.
        final AutoCompleteReturnValue returnValue;
        if (!directory && pathToParse.isEmpty() && !rawPath.startsWith(DELIMITER_STR)) {
            final Trie<AutoCompleteType> globalCommandPossibilities = globalCommands.subTrie(autoCompleteArg).map(AutoCompleteMappers.commandMapper());
            returnValue = mergeAutoCompleteReturnValues(autoCompleteArg, entryReturnValue, globalCommandPossibilities);
        } else {
            returnValue = entryReturnValue;
        }

        return returnValue;
    }

    private AutoCompleteReturnValue mergeAutoCompleteReturnValues(String prefix,
                                                                  AutoCompleteReturnValue entryReturnValue,
                                                                  Trie<AutoCompleteType> globalCommandPossibilities) {
        if (entryReturnValue.isSuccess()) {
            final AutoCompleteReturnValueSuccess success = entryReturnValue.getSuccess();
            final Trie<AutoCompleteType> entryPossibilities = success.getPossibilities();
            final Trie<AutoCompleteType> possibilities = globalCommandPossibilities.union(entryPossibilities);
            return AutoCompleteReturnValue.success(prefix, possibilities);
        } else {
            if (globalCommandPossibilities.isEmpty()) {
                return entryReturnValue;
            } else {
                return AutoCompleteReturnValue.success(prefix, globalCommandPossibilities);
            }
        }
    }

    public static boolean isLegalName(String name) {
        return !ILLEGAL_NAMES.contains(name);
    }
}

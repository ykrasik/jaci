package com.rawcod.jerminal.filesystem;

import com.google.common.base.Splitter;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:37
 */
public class FileSystemManager {
    private static final Splitter SPLITTER = Splitter.on(ShellDirectory.DELIMITER);
    private static final String DELIMITER_STR = String.valueOf(ShellDirectory.DELIMITER);

    private final ShellDirectory root;
    private final GlobalCommandManager globalCommandManager;

    private ShellDirectory currentDirectory;

    public FileSystemManager(ShellDirectory root, GlobalCommandManager globalCommandManager) {
        this.root = root;
        this.globalCommandManager = globalCommandManager;
        this.currentDirectory = root;
    }

    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(ShellDirectory directory) {
        this.currentDirectory = checkNotNull(directory, "directory");
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
        final int startingDelimiterIndex = startsFromRoot ? 1 : 0;
        final boolean endsWithDelimiter = rawPath.endsWith(DELIMITER_STR);
        final int endingDelimiterIndex = endsWithDelimiter ? Math.max(rawPath.length() - 1, startingDelimiterIndex) : rawPath.length();
        final String pathToSplit = rawPath.substring(startingDelimiterIndex, endingDelimiterIndex);

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // First check if rawPath is a global commands.
        // It may only be a global command if splitPath only has 1 entry that doesn't start from root.
        if (!directory && !startsFromRoot && splitPath.size() == 1) {
            final String rawEntry = splitPath.get(0);
            final ParseEntryReturnValue globalCommandReturnValue = globalCommandManager.parseGlobalCommand(rawEntry);
            if (globalCommandReturnValue.isSuccess()) {
                return globalCommandReturnValue;
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
        ShellDirectory dir = startsFromRoot ? root : currentDirectory;
        for (final String entry : path) {
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
        final int lastIndexOfDelimiter = rawPath.lastIndexOf(ShellDirectory.DELIMITER);
        final String pathToParse = rawPath.substring(0, lastIndexOfDelimiter != - 1 ? lastIndexOfDelimiter : 0);
        final String autoCompleteArg = rawPath.substring(lastIndexOfDelimiter + 1);

        final ShellDirectory lastDir;
        if (pathToParse.isEmpty()) {
            lastDir = currentDirectory;
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
            final AutoCompleteReturnValue globalCommandReturnValue = globalCommandManager.autoCompleteGlobalCommand(autoCompleteArg);
            returnValue = mergeAutoCompleteReturnValues(entryReturnValue, globalCommandReturnValue);
        } else {
            returnValue = entryReturnValue;
        }

        return returnValue;
    }

    private AutoCompleteReturnValue mergeAutoCompleteReturnValues(AutoCompleteReturnValue entryReturnValue,
                                                                  AutoCompleteReturnValue globalCommandReturnValue) {
        if (entryReturnValue.isSuccess()) {
            if (globalCommandReturnValue.isSuccess()) {
                // Both autoComplete operations succeeded, return a union of their suggestions.
                final AutoCompleteReturnValueSuccess entrySuccess = entryReturnValue.getSuccess();
                final AutoCompleteReturnValueSuccess globalSuccess = globalCommandReturnValue.getSuccess();
                final String prefix = entrySuccess.getPrefix();
                final TrieView union = entrySuccess.getPossibilitiesTrieView().union(globalSuccess.getPossibilitiesTrieView());
                return AutoCompleteReturnValue.success(prefix, union);
            } else {
                // Only the entry autoComplete succeeded, return it.
                return entryReturnValue;
            }
        }

        // Entry autoComplete did not succeed, check if we can instead show the global command autoComplete.
        if (globalCommandReturnValue.isSuccess()) {
            // Global command autoComplete succeeded, use it as a fallback.
            return globalCommandReturnValue;
        }

        return entryReturnValue;
    }
}

package com.rawcod.jerminal.filesystem;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
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
    private static final char DELIMITER = '/';
    private static final Splitter SPLITTER = Splitter.on(DELIMITER);

    private final ShellFileSystem fileSystem;
    private final GlobalCommandManager globalCommandManager;

    private ShellDirectory currentDirectory;

    public FileSystemManager(ShellFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.globalCommandManager = new GlobalCommandManager(fileSystem.getGlobalCommands());
        this.currentDirectory = fileSystem.getRoot();
    }

    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(ShellDirectory directory) {
        this.currentDirectory = checkNotNull(directory, "directory");
    }

    public ParseEntryReturnValue parsePathToCommand(String rawPath) {
        return parsePath(rawPath, EntryFilters.FILE_FILTER);
    }

    public ParseEntryReturnValue parsePathToDirectory(String rawPath) {
        return parsePath(rawPath, EntryFilters.DIRECTORY_FILTER);
    }

    public ParseEntryReturnValue parsePath(String rawPath) {
        return parsePath(rawPath, EntryFilters.NO_FILTER);
    }

    public ParseEntryReturnValue parsePath(String rawPath, Predicate<ShellEntry> filter) {
        final boolean startsFromRoot = !rawPath.isEmpty() && rawPath.charAt(0) == DELIMITER;
        final String pathToSplit = startsFromRoot ? rawPath.substring(1) : rawPath;

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // First check if rawPath is a global commands.
        // It may only be a global command if splitPath only has 1 entry that doesn't start from root.
        if (splitPath.size() == 1 && !startsFromRoot) {
            final String rawEntry = splitPath.get(0);
            final ParseEntryReturnValue globalCommandReturnValue = globalCommandManager.parseGlobalCommand(rawEntry, filter);
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

        final ShellDirectory dir = getLastDirReturnValue.getSuccess().getEntry().getDirectory();

        // Parse the last entry in the path according to the filter.
        final String lastEntry = splitPath.get(splitPath.size() - 1);
        final ParseEntryReturnValue returnValue = dir.getEntryManager().parseEntry(lastEntry, filter);
        if (returnValue.isFailure()) {
            return ParseEntryReturnValue.failure(returnValue.getFailure());
        }

        final ShellEntry entry = returnValue.getSuccess().getEntry();
        return ParseEntryReturnValue.success(entry);
    }

    private ParseEntryReturnValue getLastDirectory(List<String> path, boolean startsFromRoot) {
        ShellDirectory dir = startsFromRoot ? fileSystem.getRoot() : currentDirectory;
        for (String entry : path) {
            final ParseEntryReturnValue returnValue = dir.getEntryManager().parseDirectory(entry);
            if (returnValue.isFailure()) {
                // Invalid directory along the path.
                return returnValue;
            }

            dir = returnValue.getSuccess().getEntry().getDirectory();
        }

        return ParseEntryReturnValue.success(dir);
    }

    public AutoCompleteReturnValue autoCompletePathToCommand(String rawPath) {
        return autoCompletePath(rawPath, EntryFilters.FILE_FILTER);
    }

    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) {
        return autoCompletePath(rawPath, EntryFilters.DIRECTORY_FILTER);
    }

    public AutoCompleteReturnValue autoCompletePath(String rawPath) {
        return autoCompletePath(rawPath, EntryFilters.NO_FILTER);
    }

    public AutoCompleteReturnValue autoCompletePath(String rawPath, Predicate<ShellEntry> filter) {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        int lastIndexOfDelimiter = rawPath.lastIndexOf(DELIMITER);
        if (lastIndexOfDelimiter == -1) {
            // Use the whole string for autoCompletion if no delimiter present.
            lastIndexOfDelimiter = 0;
        }

        final String pathToParse = rawPath.substring(0, lastIndexOfDelimiter);
        final String autoCompleteArg = rawPath.substring(lastIndexOfDelimiter);

        final ShellDirectory lastDir;
        if (pathToParse.isEmpty()) {
            lastDir = currentDirectory;
        } else {
            final ParseEntryReturnValue returnValue = parsePathToDirectory(pathToParse);
            if (returnValue.isFailure()) {
                return AutoCompleteErrors.parseError(returnValue.getFailure());
            }
            lastDir = returnValue.getSuccess().getEntry().getDirectory();
        }

        // AutoComplete the last entry along the path.
        final AutoCompleteReturnValue entryReturnValue = lastDir.getEntryManager().autoCompleteEntry(autoCompleteArg, filter);

        // If pathToParse was empty, then autoCompleteArg could be a global command.
        final AutoCompleteReturnValue returnValue;
        if (pathToParse.isEmpty()) {
            final AutoCompleteReturnValue globalCommandReturnValue = globalCommandManager.autoCompleteGlobalCommand(autoCompleteArg, filter);
            returnValue = mergeAutoCompleteReturnValues(entryReturnValue, globalCommandReturnValue);
        } else {
            returnValue = entryReturnValue;
        }
        return returnValue;
    }

    // FIXME: Figure this out.
//    private AutoCompleteReturnValue autoCompleteEntry(ShellDirectory lastDir,
//                                                      Predicate<ShellEntry> filter,
//                                                      String rawEntry) {
//        // Let the last directory along the path autoComplete the arg.
//        final AutoCompleteReturnValue returnValue = lastDir.getEntryManager().autoCompleteEntry(rawEntry, filter);
//        if (returnValue.isFailure()) {
//            return AutoCompleteReturnValue.failure(returnValue.getFailure());
//        }
//
//
//        // A successfull autoComplete either has 1 or more possibilities.
//        // 0 possibilities is considered a failed autoComplete.
//        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
//        final TrieView possibilitiesTrieView = success.getPossibilitiesTrieView();
//        final int numPossibilities = possibilitiesTrieView.getNumWords();
//        if (numPossibilities > 1) {
//            // More then 1 possibility available, no further processing should be done here.
//            return returnValue;
//        }
//
//        // There was only 1 way of autoCompleting the entry.
//        // Let's try to be as helpful as we can:
//        // * If it's a command, add a space after.
//        // * If it's a directory, add a delimiter after.
//        final String autoCompletedArg = possibilitiesTrieView.getLongestPrefix();
//        final ParseEntryReturnValue parseEntryReturnValue = lastDir.getEntryManager().parseEntry(autoCompletedArg, context);
//        if (parseEntryReturnValue.isFailure()) {
//            // AutoComplete returned a single autoComplete possibility that gives us an invalid entry?!
//            return AutoCompleteErrors.internalError("AutoComplete suggested an invalid entry! entry='%s'", autoCompletedArg);
//        }
//
//        // Return an updated autoCompleteAddition according to the entry type.
//        final ShellEntry entry = parseEntryReturnValue.getSuccess().getEntry();
//        final char autoCompleteSuffix = entry.isDirectory() ? DELIMITER : ' ';
//        return AutoCompleteReturnValue.successSingle(autoCompleteAddition + autoCompleteSuffix);
//    }

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

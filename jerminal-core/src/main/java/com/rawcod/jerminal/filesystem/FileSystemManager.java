package com.rawcod.jerminal.filesystem;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue.ParsePathReturnValueSuccess;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 19:37
 */
public class FileSystemManager {
    private static final char DELIMITER = '/';
    private static final Splitter SPLITTER = Splitter.on(DELIMITER);

    private final ShellFileSystem fileSystem;

    public FileSystemManager(ShellFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public ParsePathReturnValue parsePathToCommand(String rawPath, ShellDirectory currentDirectory) {
        return parsePath(rawPath, currentDirectory, EntryFilters.FILE_FILTER);
    }

    public ParsePathReturnValue parsePathToDirectory(String rawPath, ShellDirectory currentDirectory) {
        return parsePath(rawPath, currentDirectory, EntryFilters.DIRECTORY_FILTER);
    }

    public ParsePathReturnValue parsePath(String rawPath, ShellDirectory currentDirectory) {
        return parsePath(rawPath, currentDirectory, EntryFilters.NO_FILTER);
    }

    public ParsePathReturnValue parsePath(String rawPath,
                                          ShellDirectory currentDirectory,
                                          Predicate<ShellEntry> filter) {
        final String pathToSplit;
        final ShellDirectory startDir;
        final boolean startsFromRoot = !rawPath.isEmpty() && rawPath.charAt(0) == DELIMITER;
        if (startsFromRoot) {
            pathToSplit = rawPath.substring(1);
            startDir = fileSystem.getRoot();
        } else {
            pathToSplit = rawPath;
            startDir = currentDirectory;
        }

        // Split the given path according to delimiter.
        final List<String> splitPath = SPLITTER.splitToList(pathToSplit);

        // Keep a record of the parsed path.
        final List<ShellDirectory> parsedPath = new ArrayList<>(splitPath.size());
        if (startsFromRoot) {
            parsedPath.add(fileSystem.getRoot());
        }

        // If the path consists of N entries, parse N-1 entries as directories
        // and parse the last entry according to the filter.
        ShellDirectory dir = startDir;
        for (int i = 0; i < splitPath.size() - 1; i++) {
            final String entry = splitPath.get(i);
            final ParseEntryReturnValue returnValue = dir.getEntryManager().parseDirectory(entry);
            if (returnValue.isFailure()) {
                // Invalid directory along the path.
                return ParsePathReturnValue.failure(returnValue.getFailure());
            }

            dir = (ShellDirectory) returnValue.getSuccess().getEntry();
            parsedPath.add(dir);
        }

        // Parse the last entry in the path according to the filter.
        final String lastEntry = splitPath.get(splitPath.size() - 1);
        final ParseEntryReturnValue returnValue = dir.getEntryManager().parseEntry(lastEntry, filter);
        if (returnValue.isFailure()) {
            return ParsePathReturnValue.failure(returnValue.getFailure());
        }

        final ShellEntry entry = returnValue.getSuccess().getEntry();
        return ParsePathReturnValue.success(parsedPath, entry);
    }

    public AutoCompleteReturnValue autoCompletePathToCommand(String rawPath, ShellDirectory currentDirectory) {
        return autoCompletePath(rawPath, currentDirectory, EntryFilters.FILE_FILTER);
    }

    public AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath, ShellDirectory currentDirectory) {
        return autoCompletePath(rawPath, currentDirectory, EntryFilters.DIRECTORY_FILTER);
    }

    public AutoCompleteReturnValue autoCompletePath(String rawPath, ShellDirectory currentDirectory) {
        return autoCompletePath(rawPath, currentDirectory, EntryFilters.NO_FILTER);
    }

    public AutoCompleteReturnValue autoCompletePath(String rawPath,
                                                    ShellDirectory currentDirectory,
                                                    Predicate<ShellEntry> filter) {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        int lastIndexOfDelimiter = rawPath.lastIndexOf(DELIMITER);
        if (lastIndexOfDelimiter == -1) {
            // Use the whole string for autoCompletion if no delimiter present.
            lastIndexOfDelimiter = 0;
        }

        final String pathToParse = rawPath.substring(0, lastIndexOfDelimiter);
        final String autoCompleteArg = rawPath.substring(lastIndexOfDelimiter);

        final ParsePathReturnValue parsePathReturnValue = parsePathToDirectory(pathToParse, currentDirectory);
        if (parsePathReturnValue.isFailure()) {
            return AutoCompleteReturnValue.parseFailure(parsePathReturnValue.getFailure());
        }

        // AutoComplete the last entry along the path.
        final ParsePathReturnValueSuccess parsePathSuccess = parsePathReturnValue.getSuccess();
        final ShellDirectory lastDir = (ShellDirectory) parsePathSuccess.getLastEntry();
        return autoCompleteEntry(lastDir, filter, autoCompleteArg);
    }

    private AutoCompleteReturnValue autoCompleteEntry(ShellDirectory lastDir,
                                                      Predicate<ShellEntry> filter,
                                                      String autoCompleteArg) {
        // Let the last directory along the path autoComplete the arg.
        final AutoCompleteReturnValue returnValue = lastDir.getEntryManager().autoCompleteEntry(autoCompleteArg, filter);
        if (returnValue.isFailure()) {
            return AutoCompleteReturnValue.failure(returnValue.getFailure());
        }

        // A successfull autoComplete either has 1 or more possibilities.
        // 0 possibilities is considered a failed autoComplete.
        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
        final List<String> possibilities = success.getPossibilities();

        // Having an empty possibilities list here is an internal error.
        if (possibilities.isEmpty()) {
            return AutoCompleteReturnValue.failure(
                AutoCompleteReturnValueFailure.internalError(
                    "Internal error: AutoComplete succeeded, but returned no possibilities!"
                )
            );
        }

        if (possibilities.size() > 1) {
            // More then 1 possibility available, no further processing should be done here.
            return returnValue;
        }

        // There was only 1 way of autoCompleting the entry.
        // Let's try to be as helpful as we can:
        // * If it's a command, add a space after.
        // * If it's a directory, add a delimiter after.
        final String autoCompleteAddition = success.getAutoCompleteAddition();
        final String autoCompletedArg = autoCompleteArg + autoCompleteAddition;
        final ParseEntryReturnValue parseEntryReturnValue = lastDir.getEntryManager().parseEntry(autoCompletedArg);
        if (parseEntryReturnValue.isFailure()) {
            // AutoComplete returned a single autoCompleteAddition that, when added to the autoCompleteArg,
            // gives us an invalid entry? Shouldn't happen.
            return AutoCompleteReturnValue.failure(
                AutoCompleteReturnValueFailure.internalError(
                    "Internal error: AutoComplete suggested an invalid entry! entry='%s'", autoCompletedArg
                )
            );
        }

        // Return an updated autoCompleteAddition according to the entry type.
        final ShellEntry entry = parseEntryReturnValue.getSuccess().getEntry();
        final char autoCompleteSuffix = entry.isDirectory() ? DELIMITER : ' ';
        return AutoCompleteReturnValue.successSingle(autoCompleteAddition + autoCompleteSuffix);
    }
}

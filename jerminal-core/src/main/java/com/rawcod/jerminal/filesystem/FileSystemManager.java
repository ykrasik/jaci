package com.rawcod.jerminal.filesystem;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue.ParsePathReturnValueSuccess;

import java.util.ArrayList;
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
    private final GlobalCommandRepository globalCommandRepository;

    private ShellDirectory currentDirectory;

    public FileSystemManager(ShellFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.globalCommandRepository = new GlobalCommandRepository(fileSystem.getGlobalCommands());
        this.currentDirectory = fileSystem.getRoot();
    }

    public ShellDirectory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(ShellDirectory directory) {
        this.currentDirectory = checkNotNull(directory, "directory is null!");
    }

    public ParsePathReturnValue parsePathToCommand(String rawPath) {
        return parsePath(rawPath, EntryFilters.FILE_FILTER);
    }

    public ParsePathReturnValue parsePathToDirectory(String rawPath) {
        return parsePath(rawPath, EntryFilters.DIRECTORY_FILTER);
    }

    public ParsePathReturnValue parsePath(String rawPath) {
        return parsePath(rawPath, EntryFilters.NO_FILTER);
    }

    public ParsePathReturnValue parsePath(String rawPath, Predicate<ShellEntry> filter) {
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
        final ParseEntryContext context = new ParseEntryContext(globalCommandRepository);
        ShellDirectory dir = startDir;
        for (int i = 0; i < splitPath.size() - 1; i++) {
            final String entry = splitPath.get(i);
            final ParseEntryReturnValue returnValue = dir.getEntryManager().parseDirectory(entry, context);
            if (returnValue.isFailure()) {
                // Invalid directory along the path.
                return ParsePathReturnValue.failure(returnValue.getFailure());
            }

            dir = returnValue.getSuccess().getEntry().getDirectory();
            parsedPath.add(dir);
        }

        // Parse the last entry in the path according to the filter.
        final String lastEntry = splitPath.get(splitPath.size() - 1);
        final ParseEntryReturnValue returnValue = dir.getEntryManager().parseEntry(lastEntry, filter, context);
        if (returnValue.isFailure()) {
            return ParsePathReturnValue.failure(returnValue.getFailure());
        }

        final ShellEntry entry = returnValue.getSuccess().getEntry();
        return ParsePathReturnValue.success(parsedPath, entry);
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

        final ParsePathReturnValue parsePathReturnValue = parsePathToDirectory(pathToParse);
        if (parsePathReturnValue.isFailure()) {
            return AutoCompleteErrors.parseError(parsePathReturnValue.getFailure());
        }

        // AutoComplete the last entry along the path.
        final ParsePathReturnValueSuccess parsePathSuccess = parsePathReturnValue.getSuccess();
        final ShellDirectory lastDir = parsePathSuccess.getLastEntry().getDirectory();
        return autoCompleteEntry(lastDir, filter, autoCompleteArg);
    }

    private AutoCompleteReturnValue autoCompleteEntry(ShellDirectory lastDir,
                                                      Predicate<ShellEntry> filter,
                                                      String rawEntry) {
        // Let the last directory along the path autoComplete the arg.
        final ParseEntryContext context = createParseEntryContext();
        final AutoCompleteReturnValue returnValue = lastDir.getEntryManager().autoCompleteEntry(rawEntry, filter, context);
        if (returnValue.isFailure()) {
            return AutoCompleteReturnValue.failure(returnValue.getFailure());
        }

        // A successfull autoComplete either has 1 or more possibilities.
        // 0 possibilities is considered a failed autoComplete.
        final AutoCompleteReturnValueSuccess success = returnValue.getSuccess();
        final List<String> possibilities = success.getSuggestions();

        // Having an empty possibilities list here is an internal error.
        if (possibilities.isEmpty()) {
            return AutoCompleteErrors.internalErrorEmptyPossibilities();
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
        final String autoCompletedArg = rawEntry + autoCompleteAddition;
        final ParseEntryReturnValue parseEntryReturnValue = lastDir.getEntryManager().parseEntry(autoCompletedArg, context);
        if (parseEntryReturnValue.isFailure()) {
            // AutoComplete returned a single autoCompleteAddition that, when added to the rawEntry,
            // gives us an invalid entry? Shouldn't happen.
            return AutoCompleteErrors.internalError(
                "Internal error: AutoComplete suggested an invalid entry! entry='%s'", autoCompletedArg
            );
        }

        // Return an updated autoCompleteAddition according to the entry type.
        final ShellEntry entry = parseEntryReturnValue.getSuccess().getEntry();
        final char autoCompleteSuffix = entry.isDirectory() ? DELIMITER : ' ';
        return AutoCompleteReturnValue.successSingle(autoCompleteAddition + autoCompleteSuffix);
    }

    private ParseEntryContext createParseEntryContext() {
        return new ParseEntryContext(globalCommandRepository);
    }
}

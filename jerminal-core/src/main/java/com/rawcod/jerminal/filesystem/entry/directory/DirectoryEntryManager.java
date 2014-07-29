package com.rawcod.jerminal.filesystem.entry.directory;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.*;
import com.rawcod.jerminal.filesystem.ParseEntryContext;
import com.rawcod.jerminal.filesystem.entry.EntryFilters;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.util.AutoCompleteUtils;

import java.util.Arrays;
import java.util.List;

/**
 * User: ykrasik
 * Date: 20/07/2014
 * Time: 21:05
 */
public class DirectoryEntryManager {
    private static final String THIS = ".";
    private static final String PARENT = "..";

    private final ShellDirectory directory;
    private final Trie<ShellEntry> children;

    public DirectoryEntryManager(ShellDirectory directory) {
        this.directory = directory;
        this.children = new TrieImpl<>();

        children.put(THIS, directory);
        children.put(PARENT, directory.getParent());
    }

    public void addChild(ShellEntry entry) {
        children.put(entry.getName(), entry);
    }

    public ParseEntryReturnValue parseCommand(String rawCommand, ParseEntryContext context) {
        return parseEntry(rawCommand, EntryFilters.FILE_FILTER, context);
    }

    public ParseEntryReturnValue parseDirectory(String rawDirectory, ParseEntryContext context) {
        return parseEntry(rawDirectory, EntryFilters.DIRECTORY_FILTER, context);
    }

    public ParseEntryReturnValue parseEntry(String rawEntry, ParseEntryContext context) {
        return parseEntry(rawEntry, EntryFilters.NO_FILTER, context);
    }

    public ParseEntryReturnValue parseEntry(String rawEntry,
                                            Predicate<ShellEntry> filter,
                                            ParseEntryContext context) {
        // First check global commands.
        final ParseEntryReturnValue globalCommandReturnValue = context.getGlobalCommandRepository().parseGlobalCommand(rawEntry, filter);
        if (globalCommandReturnValue.isSuccess()) {
            return globalCommandReturnValue;
        }

        // rawEntry didn't match a global command, check child entries.
        final ShellEntry childEntry = children.get(rawEntry);
        if (childEntry == null) {
            // Give a meaningful error message.
            if (directory.isEmpty()) {
                return ParseErrors.emptyDirectory(directory.getName());
            } else {
                return ParseErrors.directoryDoesNotContainEntry(directory.getName(), rawEntry);
            }
        }

        // Child entry exists, check that it is allowed by the filter.
        if (!filter.apply(childEntry)) {
            return ParseErrors.invalidAccessToEntry(directory.getName(), rawEntry);
        }

        return ParseEntryReturnValue.success(childEntry);
    }

    public AutoCompleteReturnValue autoCompleteCommand(String prefix, ParseEntryContext context) {
        return autoCompleteEntry(prefix, EntryFilters.FILE_FILTER, context);
    }

    public AutoCompleteReturnValue autoCompleteDirectory(String prefix, ParseEntryContext context) {
        return autoCompleteEntry(prefix, EntryFilters.DIRECTORY_FILTER, context);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String prefix, ParseEntryContext context) {
        return autoCompleteEntry(prefix, EntryFilters.NO_FILTER, context);
    }

    public AutoCompleteReturnValue autoCompleteEntry(String prefix,
                                                     Predicate<ShellEntry> filter,
                                                     ParseEntryContext context) {
        // Create the global commands word trie.
        final WordTrie globalCommandsWordTrie = context.getGlobalCommandRepository().getWordTrie(prefix, filter);

        if (prefix.isEmpty() && directory.isEmpty()) {
            // We are being asked to autoComplete an empty directory from an empty prefix.
            // This is a special case in which we only show the special characters and global commands,
            // but don't change the commandLine.
            final List<String> possibilities = Arrays.asList(THIS, PARENT);
            final List<String> globalCommands = globalCommandsWordTrie.getAllWords();
            possibilities.addAll(globalCommands);
            return AutoCompleteReturnValue.successMultiple("", possibilities);
        }

        // Create a trie from the possible children words.
        final WordTrie childrenWordTrie = Tries.getWordTrieWithFilter(children, prefix, filter);

        // Create a union between all possible auto-complete words.
        final WordTrie unionTrie = childrenWordTrie.union(globalCommandsWordTrie);

        if (unionTrie.isEmpty()) {
            // Give a meaningful error message.
            if (directory.isEmpty()) {
                return AutoCompleteErrors.emptyDirectory(directory.getName());
            } else {
                return AutoCompleteErrors.noPossibleValuesForDirectoryWithPrefix(directory.getName(), prefix);
            }
        }

        return AutoCompleteUtils.autoComplete(prefix, unionTrie);
    }
}

package com.rawcod.jerminal.autocomplete;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 17:57
 */
public class DirectoryEntryAutoCompleter extends AbstractAutoCompleter<ShellEntry> {
    private final ShellDirectory directory;

    public DirectoryEntryAutoCompleter(Trie<ShellEntry> entries, ShellDirectory directory) {
        super(entries);
        this.directory = directory;
    }

    @Override
    protected AutoCompleteReturnValueFailure noPossibleWords(String prefix) {
        // Give a meaningful error message.
        if (directory.isEmpty()) {
            return AutoCompleteReturnValueFailure.parseFailure(
                ParseReturnValueFailure.emptyDirectory(directory.getName())
            );
        }

        return AutoCompleteReturnValueFailure.from(
            AutoCompleteError.NO_POSSIBLE_VALUES,
            "AutoComplete error: No child entries possible for directory='%s', prefix='%s'", directory.getName(), prefix
        );
    }
}

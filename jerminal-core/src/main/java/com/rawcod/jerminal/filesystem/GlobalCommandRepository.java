package com.rawcod.jerminal.filesystem;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.collections.trie.WordTrie;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.Set;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 22:58
 */
public class GlobalCommandRepository {
    private final Trie<ShellEntry> globalCommandsTrie;

    public GlobalCommandRepository(Set<ShellCommand> globalCommands) {
        this.globalCommandsTrie = new TrieImpl<>();
        for (ShellCommand globalCommand : globalCommands) {
            globalCommandsTrie.put(globalCommand.getName(), globalCommand);
        }
    }

    public WordTrie getWordTrie(String prefix, Predicate<ShellEntry> filter) {
        return Tries.getWordTrieWithFilter(globalCommandsTrie, prefix, filter);
    }

    public ParseEntryReturnValue parseGlobalCommand(String rawEntry, Predicate<ShellEntry> filter) {
        final ShellEntry globalCommand = globalCommandsTrie.get(rawEntry);
        if (globalCommand != null && filter.apply(globalCommand)) {
            return ParseEntryReturnValue.success(globalCommand);
        }
        return ParseErrors.invalidGlobalCommand(rawEntry);
    }
}

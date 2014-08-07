package com.rawcod.jerminal.filesystem;

import com.google.common.base.Optional;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 22:58
 */
public class GlobalCommandManager {
    private final Map<String, ShellCommand> globalCommandsMap;
    private final Trie<ShellCommand> globalCommandsTrie;

    public GlobalCommandManager() {
        this.globalCommandsMap = new HashMap();
        this.globalCommandsTrie = new TrieImpl<>();
    }

    public void addGlobalCommand(ShellCommand globalCommand) {
        final String name = globalCommand.getName();
        globalCommandsMap.put(name, globalCommand);

        // Add a space for command autoCompletion.
        globalCommandsTrie.put(name + ' ', globalCommand);
    }

    public ParseEntryReturnValue parseGlobalCommand(String rawEntry) {
        final ShellEntry globalCommand = globalCommandsMap.get(rawEntry);
        if (globalCommand != null) {
            return ParseEntryReturnValue.success(globalCommand);
        }
        return ParseErrors.invalidGlobalCommand(rawEntry);
    }

    public AutoCompleteReturnValue autoCompleteGlobalCommand(String prefix) {
        final Optional<TrieView> globalCommandsTrieView = Tries.getTrieView(globalCommandsTrie, prefix);
        if (!globalCommandsTrieView.isPresent()) {
            return AutoCompleteErrors.noPossibleValuesForPrefix(prefix);
        }
        return AutoCompleteReturnValue.success(prefix, globalCommandsTrieView.get());
    }
}

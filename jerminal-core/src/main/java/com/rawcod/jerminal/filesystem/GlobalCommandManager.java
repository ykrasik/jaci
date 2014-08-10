package com.rawcod.jerminal.filesystem;

import com.google.common.base.Optional;
import com.rawcod.jerminal.collections.trie.Trie2;
import com.rawcod.jerminal.collections.trie.TrieBuilder;
import com.rawcod.jerminal.collections.trie.TrieView;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.Map;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 22:58
 */
public class GlobalCommandManager {
    private final Trie2<ShellCommand> globalCommands;

    public GlobalCommandManager(Map<String, ShellCommand> globalCommands) {
        this.globalCommands = new TrieBuilder<ShellCommand>().addAll(globalCommands).build();
    }

    public ParseEntryReturnValue parseGlobalCommand(String rawEntry) {
        final ShellEntry globalCommand = globalCommands.get(rawEntry);
        if (globalCommand != null) {
            return ParseEntryReturnValue.success(globalCommand);
        }
        return ParseErrors.invalidGlobalCommand(rawEntry);
    }

    public AutoCompleteReturnValue autoCompleteGlobalCommand(String prefix) {
        final Optional<TrieView> globalCommandsTrieView = Tries.getTrieView(globalCommands, prefix);
        if (!globalCommandsTrieView.isPresent()) {
            return AutoCompleteErrors.noPossibleValuesForPrefix(prefix);
        }
        return AutoCompleteReturnValue.success(prefix, globalCommandsTrieView.get());
    }
}

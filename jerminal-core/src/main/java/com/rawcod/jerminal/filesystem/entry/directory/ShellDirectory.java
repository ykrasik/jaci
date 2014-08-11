package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

import java.util.Collection;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 14:44
 */
public interface ShellDirectory extends ShellEntry {
    boolean isEmpty();
    Collection<ShellEntry> getChildren();
    ShellDirectory getParent();

    ParseEntryReturnValue parseCommand(String rawCommand);
    ParseEntryReturnValue parseDirectory(String rawDirectory);

    AutoCompleteReturnValue autoCompleteDirectory(String prefix);
    AutoCompleteReturnValue autoCompleteEntry(String prefix);
}

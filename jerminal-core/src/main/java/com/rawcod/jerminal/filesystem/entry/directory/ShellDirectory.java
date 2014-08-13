package com.rawcod.jerminal.filesystem.entry.directory;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;

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

    ShellCommand parseCommand(String rawCommand) throws ParseException;
    ShellDirectory parseDirectory(String rawDirectory) throws ParseException;

    Trie<AutoCompleteType> autoCompleteDirectory(String prefix) throws ParseException;
    Trie<AutoCompleteType> autoCompleteEntry(String prefix) throws ParseException;
}

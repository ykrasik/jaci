package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 14/08/2014
 * Time: 18:36
 */
public interface ShellFileSystem {
    ShellDirectory getRoot();

    ShellDirectory getCurrentDirectory();
    void setCurrentDirectory(ShellDirectory directory);

    ShellCommand parsePathToCommand(String rawPath) throws ParseException;

    ShellDirectory parsePathToDirectory(String rawPath) throws ParseException;

    AutoCompleteReturnValue autoCompletePathToDirectory(String rawPath) throws ParseException;

    AutoCompleteReturnValue autoCompletePath(String rawPath) throws ParseException;
}

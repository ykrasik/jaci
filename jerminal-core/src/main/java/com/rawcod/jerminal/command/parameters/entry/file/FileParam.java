package com.rawcod.jerminal.command.parameters.entry.file;

import com.rawcod.jerminal.command.parameters.entry.AbstractEntryCommandParam;
import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class FileParam extends AbstractEntryCommandParam {
    public FileParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected ParsePathReturnValue doParse(String rawValue,
                                           FileSystemManager fileSystemManager,
                                           ShellDirectory currentDirectory) {
        return fileSystemManager.parsePathToCommand(rawValue, currentDirectory);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix,
                                                     FileSystemManager fileSystemManager,
                                                     ShellDirectory currentDirectory) {
        return fileSystemManager.autoCompletePathToCommand(prefix, currentDirectory);
    }

    @Override
    public String toString() {
        return String.format("{%s: file}", getName());
    }
}

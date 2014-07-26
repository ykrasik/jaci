package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class DirectoryParam extends AbstractEntryCommandParam {
    public DirectoryParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected ParsePathReturnValue doParse(String rawValue,
                                           FileSystemManager fileSystemManager,
                                           ShellDirectory currentDirectory) {
        return fileSystemManager.parsePathToDirectory(rawValue, currentDirectory);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix,
                                                     FileSystemManager fileSystemManager,
                                                     ShellDirectory currentDirectory) {
        return fileSystemManager.autoCompletePathToDirectory(prefix, currentDirectory);
    }

    @Override
    public String toString() {
        return String.format("{%s: directory}", getName());
    }
}

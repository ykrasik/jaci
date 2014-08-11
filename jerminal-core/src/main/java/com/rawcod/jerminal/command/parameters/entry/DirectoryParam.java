package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class DirectoryParam extends AbstractEntryCommandParam {
    public DirectoryParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "directory";
    }

    @Override
    protected ParseEntryReturnValue doParse(String rawValue, ShellFileSystem fileSystem) {
        return fileSystem.parsePathToDirectory(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix, ShellFileSystem fileSystem) {
        return fileSystem.autoCompletePathToDirectory(prefix);
    }
}

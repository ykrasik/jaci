package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class FileParam extends AbstractEntryCommandParam {
    public FileParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "file";
    }

    @Override
    protected ParseEntryReturnValue doParse(String rawValue, ShellFileSystem fileSystem) {
        return fileSystem.parsePathToCommand(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix, ShellFileSystem fileSystem) {
        return fileSystem.autoCompletePath(prefix);
    }
}

package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.filesystem.FileSystemManager;
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
    protected String getExternalFormType() {
        return "file";
    }

    @Override
    protected ParsePathReturnValue doParse(String rawValue, FileSystemManager fileSystemManager) {
        return fileSystemManager.parsePathToCommand(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix, FileSystemManager fileSystemManager) {
        return fileSystemManager.autoCompletePathToCommand(prefix);
    }
}

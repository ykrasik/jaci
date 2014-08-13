package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class DirectoryParam extends AbstractEntryCommandParam<ShellDirectory> {
    public DirectoryParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "directory";
    }

    @Override
    protected ShellDirectory doParse(String rawValue, ShellFileSystem fileSystem) throws ParseException {
        return fileSystem.parsePathToDirectory(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix, ShellFileSystem fileSystem) throws ParseException {
        return fileSystem.autoCompletePathToDirectory(prefix);
    }
}

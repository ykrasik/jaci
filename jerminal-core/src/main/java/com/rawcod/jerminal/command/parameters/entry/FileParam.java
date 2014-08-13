package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class FileParam extends AbstractEntryCommandParam<ShellCommand> {
    public FileParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "file";
    }

    @Override
    protected ShellCommand doParse(String rawValue, ShellFileSystem fileSystem) throws ParseException {
        return fileSystem.parsePathToCommand(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue doAutoComplete(String prefix, ShellFileSystem fileSystem) throws ParseException {
        return fileSystem.autoCompletePath(prefix);
    }
}

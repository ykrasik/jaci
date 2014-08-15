package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class DirectoryParam extends AbstractMandatoryCommandParam<ShellDirectory> {
    private final ShellFileSystem fileSystem;

    public DirectoryParam(String name, String description, ShellFileSystem fileSystem) {
        super(name, description);
        this.fileSystem = checkNotNull(fileSystem, "fileSystem");
    }

    @Override
    protected String getExternalFormType() {
        return "directory";
    }

    @Override
    public Object parse(String rawValue) throws ParseException {
        return fileSystem.parsePathToDirectory(rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        return fileSystem.autoCompletePathToDirectory(prefix);
    }
}

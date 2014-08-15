package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class FileParam extends AbstractMandatoryCommandParam<ShellCommand> {
    private final ShellFileSystem fileSystem;

    public FileParam(String name, String description, ShellFileSystem fileSystem) {
        super(name, description);
        this.fileSystem = checkNotNull(fileSystem, "fileSystem");
    }

    @Override
    protected String getExternalFormType() {
        return "file";
    }

    @Override
    public Object parse(String rawValue) throws ParseException {
        return fileSystem.parsePathToCommand(rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        return fileSystem.autoCompletePath(prefix);
    }
}

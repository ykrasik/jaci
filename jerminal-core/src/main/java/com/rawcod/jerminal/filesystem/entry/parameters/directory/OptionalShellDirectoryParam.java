package com.rawcod.jerminal.filesystem.entry.parameters.directory;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.filesystem.entry.parameters.OptionalShellParam;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class OptionalShellDirectoryParam extends ShellDirectoryParam implements OptionalShellParam {
    private ShellManager manager;

    public OptionalShellDirectoryParam(String name) {
        super(name);
    }

    @Override
    public void install(ShellManager manager) {
        this.manager = manager;
        super.install(manager);
    }

    @Override
    public ParseReturnValue<ShellDirectory> parse(String arg) {
        if (arg != null) {
            return super.parse(arg);
        }

        // Value not provided, use current directory
        return ParseReturnValue.success(manager.getCurrentDirectory());
    }
}

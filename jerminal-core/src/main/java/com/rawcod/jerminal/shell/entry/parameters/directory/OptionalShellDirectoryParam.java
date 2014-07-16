package com.rawcod.jerminal.shell.entry.parameters.directory;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.directory.ShellDirectory;
import com.rawcod.jerminal.shell.entry.parameters.OptionalShellParam;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

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
    public ShellParseReturnValue<ShellDirectory> parse(String arg) {
        if (arg != null) {
            return super.parse(arg);
        }

        // Value not provided, use current directory
        return ShellParseReturnValue.success(manager.getCurrentDirectory());
    }
}

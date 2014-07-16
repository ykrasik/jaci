package com.rawcod.jerminal.shell.entry.parameters.directory;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.directory.ShellDirectory;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.parser.ShellFileParser;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class ShellDirectoryParam implements ShellParam {
    private final String name;

    private ShellFileParser parser;

    public ShellDirectoryParam(String name) {
        this.name = name;
    }

    @Override
    public void install(ShellManager manager) {
        this.parser = new ShellFileParser(manager);
    }

    @Override
    public ShellAutoCompleteReturnValue autoComplete(String arg) {
        return parser.autoCompleteDirectory(arg);
    }

    @Override
    public ShellParseReturnValue<ShellDirectory> parse(String arg) {
        if (arg == null) {
            return ShellParseReturnValue.failureMissingArgument(name);
        }

        return parser.parseDirectory(arg);
    }

    @Override
    public String toString() {
        return String.format("{%s: directory}", name);
    }
}

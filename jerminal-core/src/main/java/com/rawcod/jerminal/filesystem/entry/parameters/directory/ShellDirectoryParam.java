package com.rawcod.jerminal.filesystem.entry.parameters.directory;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.shell.parser.ShellFileParser;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

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
    public AutoCompleteReturnValue autoComplete(String arg) {
        return parser.autoCompleteDirectory(arg);
    }

    @Override
    public ParseReturnValue<ShellDirectory> parse(String arg) {
        if (arg == null) {
            return ParseReturnValue.failureMissingArgument(name);
        }

        return parser.parseDirectory(arg);
    }

    @Override
    public String toString() {
        return String.format("{%s: directory}", name);
    }
}

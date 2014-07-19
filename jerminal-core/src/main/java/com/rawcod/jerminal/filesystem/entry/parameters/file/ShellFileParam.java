package com.rawcod.jerminal.filesystem.entry.parameters.file;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.shell.parser.ShellFileParser;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ShellFileParam implements ShellParam {
    private final String name;

    private ShellFileParser parser;

    public ShellFileParam(String name) {
        this.name = name;
    }

    @Override
    public void install(ShellManager manager) {
        this.parser = new ShellFileParser(manager);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String arg) {
        return parser.autoCompleteEntry(arg);
    }

    @Override
    public ParseReturnValue<ShellCommand> parse(String arg) {
        if (arg == null) {
            return ParseReturnValue.failureMissingArgument(name);
        }

        return parser.parseCommand(arg);
    }

    @Override
    public String toString() {
        return String.format("{%s: file}", name);
    }
}

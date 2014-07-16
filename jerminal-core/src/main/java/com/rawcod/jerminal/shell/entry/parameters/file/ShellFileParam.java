package com.rawcod.jerminal.shell.entry.parameters.file;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.command.ShellCommand;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.parser.ShellFileParser;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

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
    public ShellAutoCompleteReturnValue autoComplete(String arg) {
        return parser.autoCompleteEntry(arg);
    }

    @Override
    public ShellParseReturnValue<ShellCommand> parse(String arg) {
        if (arg == null) {
            return ShellParseReturnValue.failureMissingArgument(name);
        }

        return parser.parseCommand(arg);
    }

    @Override
    public String toString() {
        return String.format("{%s: file}", name);
    }
}

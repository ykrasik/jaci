package com.rawcod.jerminal.shell.parser;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.command.ShellCommand;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ShellCommandParser {
    private final ShellStringParser<ShellCommand> globalCommands;
    private final ShellFileParser fileParser;

    public ShellCommandParser(ShellManager manager) {
        final String autoCompleteErrorFormat = "There are no commands starting with '%s'";
        final String parseErrorFormat = "Invalid command: '%s'";
        this.globalCommands = new ShellStringParser<>(autoCompleteErrorFormat, parseErrorFormat);
        this.fileParser = new ShellFileParser(manager);
    }

    public void addGlobalCommand(ShellCommand command) {
        globalCommands.addWord(command.getName(), command);
    }

    public ShellAutoCompleteReturnValue autoComplete(String arg) {
//        if (!arg.contains("/")) {
//            // Arg doesn't contain a '/', may be a global command or a file.
//            final ShellAutoCompleteReturnValue returnValue = globalCommands.autoComplete(arg);
//        }

        // Arg contains a '/', can only be a file.
        final ShellAutoCompleteReturnValue returnValue = fileParser.autoCompleteEntry(arg);
        if (!returnValue.isSuccess()) {
            return ShellAutoCompleteReturnValue.failureInvalidCommand(returnValue.getErrorMessage(), returnValue.getAutoComplete());
        }
        return returnValue;
    }

    public ShellParseReturnValue<ShellCommand> parse(String arg) {
        if (!arg.contains("/")) {
            final ShellParseReturnValue<ShellCommand> returnValue = globalCommands.parse(arg);
            if (returnValue.isSuccess()) {
                return returnValue;
            }
        }
        final ShellParseReturnValue<ShellCommand> returnValue = fileParser.parseCommand(arg);
        if (!returnValue.isSuccess()) {
            return ShellParseReturnValue.failureInvalidCommand(returnValue.getErrorMessage(), returnValue.getAutoComplete());
        }
        return returnValue;
    }
}

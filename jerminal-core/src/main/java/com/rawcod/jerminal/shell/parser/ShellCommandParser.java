package com.rawcod.jerminal.shell.parser;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

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

    public AutoCompleteReturnValue autoComplete(String arg) {
//        if (!arg.contains("/")) {
//            // Arg doesn't contain a '/', may be a global command or a file.
//            final ShellAutoCompleteReturnValue returnValue = globalCommands.autoComplete(arg);
//        }

        // Arg contains a '/', can only be a file.
        final AutoCompleteReturnValue returnValue = fileParser.autoCompleteEntry(arg);
        if (!returnValue.isSuccess()) {
            return AutoCompleteReturnValue.failureInvalidCommand(returnValue.getErrorMessage(), returnValue.getAutoComplete());
        }
        return returnValue;
    }

    public ParseReturnValue<ShellCommand> parse(String arg) {
        if (!arg.contains("/")) {
            final ParseReturnValue<ShellCommand> returnValue = globalCommands.parse(arg);
            if (returnValue.isSuccess()) {
                return returnValue;
            }
        }
        final ParseReturnValue<ShellCommand> returnValue = fileParser.parseCommand(arg);
        if (!returnValue.isSuccess()) {
            return ParseReturnValue.failureInvalidCommand(returnValue.getErrorMessage(), returnValue.getAutoComplete());
        }
        return returnValue;
    }
}

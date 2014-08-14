package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.OutputBuffer;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.exception.ExecuteException;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:41
 */
public interface ShellCommand extends ShellEntry {
    List<CommandParam> getParams();

    CommandArgs parseCommandArgs(List<String> args) throws ParseException;
    AutoCompleteReturnValue autoCompleteArgs(List<String> args) throws ParseException;

    void execute(CommandArgs args, OutputBuffer output) throws ExecuteException;
}

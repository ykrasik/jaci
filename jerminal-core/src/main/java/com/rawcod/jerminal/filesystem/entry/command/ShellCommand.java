package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.manager.CommandParamManager;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:41
 */
public interface ShellCommand extends ShellEntry {
    List<CommandParam> getParams();

    CommandParamManager getParamManager();

    ExecuteReturnValue execute(CommandArgs args);
}

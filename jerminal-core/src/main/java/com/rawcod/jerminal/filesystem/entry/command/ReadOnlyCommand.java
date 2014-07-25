package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:41
 */
public interface ReadOnlyCommand extends ShellEntry {
    List<CommandParam> getParams();
}

package com.rawcod.jerminal.command;

import com.rawcod.jerminal.exception.ExecuteException;

/**
 * User: ykrasik
 * Date: 14/08/14
 * Time: 19:11
 */
public interface CommandExecutor {
    void execute(CommandArgs args, OutputBuffer output) throws ExecuteException;
}

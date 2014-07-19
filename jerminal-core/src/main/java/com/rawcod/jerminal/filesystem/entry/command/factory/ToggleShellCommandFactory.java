package com.rawcod.jerminal.filesystem.entry.command.factory;

import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandArgs;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.filesystem.entry.parameters.ShellParamDefaultValueProvider;
import com.rawcod.jerminal.filesystem.entry.parameters.bool.OptionalBoolShellParam;

import java.util.Set;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public final class ToggleShellCommandFactory {
    public static ShellCommand create(final String command, String description, final Accessor accessor) {
        final ShellParam[] params = {
            new OptionalBoolShellParam("toggle", new ShellParamDefaultValueProvider<Boolean>() {
                @Override
                public Boolean getDefaultValue() {
                    return !accessor.get();
                }
            })
        };
        return new ShellCommand(command, description, params, new CommandExecutor() {
            @Override
            protected ExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
                final boolean toggle = args.popBool();
                accessor.set(toggle);
                return success("%s: %s", command, toggle);
            }
        });
    }

    public interface Accessor {
        void set(boolean toggle);
        boolean get();
    }

    private ToggleShellCommandFactory() { }
}

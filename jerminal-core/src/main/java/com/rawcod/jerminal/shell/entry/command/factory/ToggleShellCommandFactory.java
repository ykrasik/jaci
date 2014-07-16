package com.rawcod.jerminal.shell.entry.command.factory;

import com.rawcod.jerminal.shell.returnvalue.ShellExecuteReturnValue;
import com.rawcod.jerminal.shell.entry.command.ShellCommand;
import com.rawcod.jerminal.shell.entry.command.ShellCommandArgs;
import com.rawcod.jerminal.shell.entry.command.ShellCommandExecutor;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.entry.parameters.ShellParamDefaultValueProvider;
import com.rawcod.jerminal.shell.entry.parameters.bool.OptionalBoolShellParam;

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
        return new ShellCommand(command, description, params, new ShellCommandExecutor() {
            @Override
            protected ShellExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
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

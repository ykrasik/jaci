package com.rawcod.jerminal.filesystem.entry.command.factory;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecutionContext;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public final class ToggleShellCommandFactory {
    private static final String PARAM_NAME = "state";

    private ToggleShellCommandFactory() { }

    public static ShellCommand create(final String name,
                                      String description,
                                      final StateAccessor accessor) {
        return new ShellCommandBuilder(name)
            .setDescription(description)
            .addParam(Params.optionalBooleanParam(PARAM_NAME, "toggle", new AccessorDefaultValueProvider(accessor)))
            .setExecutor(new CommandExecutor() {
                @Override
                public ExecuteReturnValue execute(CommandArgs args, ExecutionContext context) {
                    final boolean toggle = args.getBool(PARAM_NAME);
                    accessor.set(toggle);
                    return success("%s: %s", name, toggle);
                }
            })
            .build();
    }

    public interface StateAccessor {
        void set(boolean toggle);
        boolean get();
    }

    private static class AccessorDefaultValueProvider implements Supplier<Boolean> {
        private final StateAccessor accessor;

        private AccessorDefaultValueProvider(StateAccessor accessor) {
            this.accessor = accessor;
        }

        @Override
        public Boolean get() {
            return accessor.get();
        }
    }
}

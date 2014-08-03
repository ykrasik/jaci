package com.rawcod.jerminal.command.factory;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecutionContext;
import com.rawcod.jerminal.command.parameters.bool.BooleanParamBuilder;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public final class ToggleCommandFactory {
    private static final String PARAM_NAME = "state";

    private ToggleCommandFactory() { }

    public static ShellCommand create(final String name,
                                      String description,
                                      final StateAccessor accessor) {
        return new ShellCommandBuilder(name)
            .setDescription(description)
            .addParam(new BooleanParamBuilder(PARAM_NAME)
                .setDescription("toggle")
                .setOptional(new AccessorDefaultValueProvider(accessor))
                .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public ExecutorReturnValue execute(CommandArgs args, ExecutionContext context) {
                    final boolean toggle = args.getBool(PARAM_NAME);
                    accessor.set(toggle);
                    context.println("%s: %s", name, toggle);
                    return success();
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

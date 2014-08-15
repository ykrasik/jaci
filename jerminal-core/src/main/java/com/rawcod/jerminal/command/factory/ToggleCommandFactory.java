package com.rawcod.jerminal.command.factory;

import com.google.common.base.Supplier;
import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputBuffer;
import com.github.ykrasik.jerminal.api.command.parameter.bool.BooleanParamBuilder;
import com.rawcod.jerminal.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.api.command.ShellCommandBuilder;

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
                public void execute(CommandArgs args, OutputBuffer output) throws ExecuteException {
                    final boolean toggle = args.getBool(PARAM_NAME);
                    accessor.set(toggle);
                    output.println("%s: %s", name, toggle);
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

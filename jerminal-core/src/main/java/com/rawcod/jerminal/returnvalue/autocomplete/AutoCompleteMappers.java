package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Function;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamType;
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.github.ykrasik.jerminal.api.command.ShellCommand;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 09:44
 */
public final class AutoCompleteMappers {
    private static final Function<ShellCommand, AutoCompleteType> COMMAND_MAPPER = new Function<ShellCommand, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(ShellCommand input) {
            return AutoCompleteType.COMMAND;
        }
    };

    private static final Function<ShellEntry, AutoCompleteType> ENTRY_MAPPER = new Function<ShellEntry, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(ShellEntry input) {
            if (input.isDirectory()) {
                return AutoCompleteType.DIRECTORY;
            } else {
                return AutoCompleteType.COMMAND;
            }
        }
    };

    private static final Function<CommandParam, AutoCompleteType> COMMAND_PARAM_MAPPER = new Function<CommandParam, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(CommandParam input) {
            return input.getType() == ParamType.FLAG ? AutoCompleteType.COMMAND_PARAM_FLAG : AutoCompleteType.COMMAND_PARAM_NAME;
        }
    };

    private static final Function<String, AutoCompleteType> COMMAND_PARAM_VALUE_STRING_MAPPER = new Function<String, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(String input) {
            return AutoCompleteType.COMMAND_PARAM_VALUE;
        }
    };

    private AutoCompleteMappers() {
    }

    public static Function<ShellCommand, AutoCompleteType> commandMapper() {
        return COMMAND_MAPPER;
    }

    public static Function<ShellEntry, AutoCompleteType> entryMapper() {
        return ENTRY_MAPPER;
    }

    public static Function<CommandParam, AutoCompleteType> commandParamNameMapper() {
        return COMMAND_PARAM_MAPPER;
    }

    public static Function<String, AutoCompleteType> commandParamValueStringMapper() {
        return COMMAND_PARAM_VALUE_STRING_MAPPER;
    }
}

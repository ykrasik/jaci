/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.param;

import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A {@link CliParam} that parses {@link CliCommand} values.<br>
 * Not a part of the official API - this is a CLI-only param which doesn't have a ParamDef,
 * which is why it can only be constructed through the {@link Builder}.
 *
 * @author Yevgeny Krasik
 */
public class CommandCliParam extends AbstractCliParam<CliCommand> {
    private final CliCommandHierarchy hierarchy;

    private CommandCliParam(Identifier identifier,
                            Opt<Supplier<CliCommand>> defaultValueSupplier,
                            @NonNull CliCommandHierarchy hierarchy) {
        super(identifier, defaultValueSupplier);

        this.hierarchy = hierarchy;
    }

    @Override
    protected String getParamTypeName() {
        return "directory";
    }

    @Override
    public CliCommand parse(@NonNull String rawValue) throws ParseException {
        return hierarchy.parsePathToCommand(rawValue);
    }

    @Override
    public AutoComplete autoComplete(@NonNull String prefix) throws ParseException {
        return hierarchy.autoCompletePath(prefix);
    }

    // TODO: JavaDoc - mention that this has a builder and other don't, because this is a cli-only param that doesn't have
    // TODO: a def counterpart.
    @Accessors(chain = true)
    public static class Builder {
        private final String name;
        private final CliCommandHierarchy hierarchy;

        @Setter
        @NonNull private String description = "command";

        private Opt<Supplier<CliCommand>> defaultValueSupplier = Opt.absent();

        public Builder(@NonNull String name, @NonNull CliCommandHierarchy hierarchy) {
            this.name = name;
            this.hierarchy = hierarchy;
        }

        public Builder setOptional(@NonNull CliCommand defaultValue) {
            return setOptional(Suppliers.of(defaultValue));
        }

        public Builder setOptional(@NonNull Supplier<CliCommand> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        public CommandCliParam build() {
            final Identifier identifier = new Identifier(name, description);
            return new CommandCliParam(identifier, defaultValueSupplier, hierarchy);
        }
    }
}

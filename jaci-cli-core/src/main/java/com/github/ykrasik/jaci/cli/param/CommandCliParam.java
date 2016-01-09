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

package com.github.ykrasik.jaci.cli.param;

import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.cli.assist.AutoComplete;
import com.github.ykrasik.jaci.cli.command.CliCommand;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.cli.hierarchy.CliCommandHierarchy;
import com.github.ykrasik.jaci.util.function.MoreSuppliers;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.Objects;

/**
 * A {@link CliParam} that parses {@link CliCommand} values.
 * Not a part of the official API - this is a CLI-only param which doesn't have a ParamDef,
 * which is why it can only be constructed through the {@link Builder}.
 *
 * @author Yevgeny Krasik
 */
public class CommandCliParam extends AbstractCliParam<CliCommand> {
    private final CliCommandHierarchy hierarchy;

    private CommandCliParam(Identifier identifier,
                            Opt<Spplr<CliCommand>> defaultValueSupplier,
                            CliCommandHierarchy hierarchy) {
        super(identifier, defaultValueSupplier);

        this.hierarchy = Objects.requireNonNull(hierarchy, "hierarchy");
    }

    @Override
    protected String getValueTypeName() {
        return "directory";
    }

    @Override
    public CliCommand parse(String arg) throws ParseException {
        return hierarchy.parsePathToCommand(arg);
    }

    @Override
    public AutoComplete autoComplete(String prefix) throws ParseException {
        return hierarchy.autoCompletePath(prefix);
    }

    /**
     * A builder for a {@link CommandCliParam}.
     */
    public static class Builder {
        private final String name;
        private final CliCommandHierarchy hierarchy;
        private String description = "command";
        private Opt<Spplr<CliCommand>> defaultValueSupplier = Opt.absent();

        public Builder(String name, CliCommandHierarchy hierarchy) {
            this.name = Objects.requireNonNull(name, "name");
            this.hierarchy = Objects.requireNonNull(hierarchy, "hierarchy");
        }

        /**
         * @param description Parameter description.
         * @return {@code this}, for chaining.
         */
        public Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        /**
         * Set this parameter to be optional, and return the given {@link CliCommand} if it is not passed.
         *
         * @param defaultValue {@link CliCommand} to return if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(CliCommand defaultValue) {
            return setOptional(MoreSuppliers.of(Objects.requireNonNull(defaultValue, "defaultValue")));
        }

        /**
         * Set this parameter to be optional, and invoke the given {@link Spplr} for a default value if it is not passed.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(Spplr<CliCommand> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        /**
         * @return A {@link CommandCliParam} built out of this builder's parameters.
         */
        public CommandCliParam build() {
            final Identifier identifier = new Identifier(name, description);
            return new CommandCliParam(identifier, defaultValueSupplier, hierarchy);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("name='").append(name).append('\'');
            sb.append(", hierarchy=").append(hierarchy);
            sb.append(", description='").append(description).append('\'');
            sb.append(", defaultValueSupplier=").append(defaultValueSupplier);
            sb.append('}');
            return sb.toString();
        }
    }
}

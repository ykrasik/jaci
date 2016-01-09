/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.command;

import com.github.ykrasik.jaci.Identifiable;
import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.param.ParamDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A definition for a command.
 * Contains the command's name, description, parameters and
 * executor that can execute code given argument values for those parameters.<br>
 * Built through the {@link CommandDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class CommandDef implements Identifiable {
    private final Identifier identifier;
    private final List<ParamDef<?>> paramDefs;
    private final CommandExecutor executor;

    private CommandDef(Identifier identifier, List<ParamDef<?>> paramDefs, CommandExecutor executor) {
        this.identifier = identifier;
        this.paramDefs = paramDefs;
        this.executor = executor;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * @return The command's parameter definitions.
     */
    public List<ParamDef<?>> getParamDefs() {
        return paramDefs;
    }

    /**
     * @return The command's executor.
     */
    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    /**
     * A builder for a {@link CommandDef}.
     */
    public static class Builder {
        private final String name;
        private final CommandExecutor executor;

        private String description = "command";
        private final List<ParamDef<?>> paramDefs = new ArrayList<>(4);

        /**
         * @param name Command name.
         * @param executor Command executor.
         */
        public Builder(String name, CommandExecutor executor) {
            this.name = Objects.requireNonNull(name, "name");
            this.executor = Objects.requireNonNull(executor, "executor");
        }

        /**
         * Set the command's description.
         *
         * @param description Description to set.
         * @return {@code this}, for chaining.
         */
        public Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        /**
         * Add a parameter definition to this command.
         *
         * @param paramDef Parameter definition to add.
         * @return {@code this}, for chaining.
         */
        public Builder addParam(ParamDef<?> paramDef) {
            this.paramDefs.add(Objects.requireNonNull(paramDef, "paramDef"));
            return this;
        }

        /**
         * @return A {@link CommandDef} built out of this builder's parameters.
         */
        public CommandDef build() {
            return new CommandDef(new Identifier(name, description), Collections.unmodifiableList(new ArrayList<>(paramDefs)), executor);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("name='").append(name).append('\'');
            sb.append(", executor=").append(executor);
            sb.append(", description='").append(description).append('\'');
            sb.append(", paramDefs=").append(paramDefs);
            sb.append('}');
            return sb.toString();
        }
    }
}


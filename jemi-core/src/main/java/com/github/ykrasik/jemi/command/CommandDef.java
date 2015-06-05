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

package com.github.ykrasik.jemi.command;

import com.github.ykrasik.jemi.Identifiable;
import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.param.ParamDef;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A definition for a command.
 * Contains the command's name, description, parameters and
 * executor that can execute code given argument values for those parameters.<br>
 * Built through the {@link CommandDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandDef implements Identifiable {
    private final Identifier identifier;
    private final List<ParamDef<?>> paramDefs;
    private final CommandExecutor executor;

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
    @ToString
    public static class Builder {
        private final String name;
        private final CommandExecutor executor;

        private String description = "command";
        private final List<ParamDef<?>> paramDefs = new ArrayList<>(4);

        public Builder(@NonNull String name, @NonNull CommandExecutor executor) {
            this.name = name;
            this.executor = executor;
        }

        /**
         * Set the command's description.
         *
         * @param description Description to set.
         * @return this, for chaining.
         */
        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        /**
         * Add a parameter definition to this command.
         *
         * @param paramDef Parameter definition to add.
         * @return this, for chaining.
         */
        public Builder addParam(@NonNull ParamDef<?> paramDef) {
            this.paramDefs.add(paramDef);
            return this;
        }

        /**
         * @return A {@link CommandDef} built out of this builder's parameters.
         */
        public CommandDef build() {
            return new CommandDef(new Identifier(name, description), Collections.unmodifiableList(new ArrayList<>(paramDefs)), executor);
        }
    }
}


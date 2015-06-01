/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jemi.core.command;

import com.github.ykrasik.jemi.core.Identifiable;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.core.param.ParamDef;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A definition for a command.<br>
 * Defines the command parameters in the order they appear and can execute code given arguments for those parameters.
 *
 * @author Yevgeny Krasik
 */
// TODO: Wrong JavaDoc
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandDef implements Identifiable {
    // TODO: Javadoc
    @NonNull private final Identifier identifier;

    /**
     * The command's declared parameters.
     */
    @NonNull private final List<ParamDef<?>> paramDefs;

    // TODO: Javadoc
    @NonNull private final CommandExecutor executor;

    // TODO: JavaDoc
    public String getName() {
        return identifier.getName();
    }

    // TODO: JavaDoc
    public String getDescription() {
        return identifier.getDescription();
    }

    @Override
    public String toString() {
        return identifier.toString();
    }

    // TODO: JavaDoc
    public static class Builder {
        private final String name;

        private String description = "command";
        private CommandExecutor executor;

        private final List<ParamDef<?>> paramDefs = new ArrayList<>(4);

        public Builder(@NonNull String name) {
            this.name = name;
        }

        public CommandDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new CommandDef(identifier, Collections.unmodifiableList(new ArrayList<>(paramDefs)), executor);
        }

        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        public Builder setExecutor(@NonNull CommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        public Builder addParam(@NonNull ParamDef<?> param) {
            this.paramDefs.add(param);
            return this;
        }
    }
}


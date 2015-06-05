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

package com.github.ykrasik.jemi.hierarchy;

import com.github.ykrasik.jemi.command.CommandDef;
import com.github.ykrasik.jemi.directory.CommandDirectoryDef;
import com.github.ykrasik.jemi.path.ParsedPath;
import com.github.ykrasik.jemi.reflection.ReflectionClassProcessor;
import com.github.ykrasik.jemi.util.reflection.ReflectionUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A hierarchy of {@link CommandDirectoryDef}s and {@link CommandDef}s.
 * Essentially, this is a file system starting from a single 'root' directory.
 * Built through the {@link CommandHierarchy.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandHierarchy {
    private final CommandDirectoryDef root;

    /**
     * @return Root {@link CommandDirectoryDef}.
     */
    public CommandDirectoryDef getRoot() {
        return root;
    }

    /**
     * A builder for a {@link CommandHierarchy}.
     */
    @ToString
    public static class Builder {
        private static final ReflectionClassProcessor PROCESSOR = new ReflectionClassProcessor();

        private final CommandDirectoryDef.Builder root = new CommandDirectoryDef.Builder("root").setDescription("root");

        /**
         * Process a class and add any commands defined through annotations to this builder.
         * Class must have a no-args constructor.
         *
         * @param clazz Class to process.
         * @return {@code this}, for chaining.
         */
        public Builder processClass(@NonNull Class<?> clazz) {
            final Object instance = ReflectionUtils.createInstanceNoArgs(clazz);
            return processObject(instance);
        }

        /**
         * Process the object's class and add any commands defined through annotations to this builder.
         *
         * @param instance Object whose class to process.
         * @return {@code this}, for chaining.
         */
        public Builder processObject(@NonNull Object instance) {
            final Map<ParsedPath, List<CommandDef>> pathToCommandDefsMap = PROCESSOR.processObject(instance);

            // Add the returned commands to the hierarchy.
            for (Entry<ParsedPath, List<CommandDef>> entry : pathToCommandDefsMap.entrySet()) {
                addCommandDefs(entry.getKey(), entry.getValue());
            }
            return this;
        }

        private void addCommandDefs(ParsedPath path, List<CommandDef> commandDefs) {
            // Advance along the path, creating directories as necessary.
            CommandDirectoryDef.Builder dir = root;
            for (String name : path) {
                dir = dir.getOrCreateDirectory(name);
            }
            dir.addCommandDefs(commandDefs);
        }

        /**
         * @return A {@link CommandHierarchy} built out of this builder's parameters.
         */
        public CommandHierarchy build() {
            final CommandDirectoryDef rootDef = root.build();
            return new CommandHierarchy(rootDef);
        }
    }
}

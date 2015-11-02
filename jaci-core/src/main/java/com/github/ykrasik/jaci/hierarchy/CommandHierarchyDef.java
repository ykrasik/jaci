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

package com.github.ykrasik.jaci.hierarchy;

import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.directory.CommandDirectoryDef;
import com.github.ykrasik.jaci.path.ParsedPath;
import com.github.ykrasik.jaci.reflection.ReflectionClassProcessor;
import com.github.ykrasik.jaci.util.reflection.ReflectionUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A definition for a hierarchy of {@link CommandDirectoryDef}s and {@link CommandDef}s.
 * Essentially, this is a file system starting from a single 'root' directory.
 * Built through the {@link CommandHierarchyDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandHierarchyDef {
    private final CommandDirectoryDef root;

    /**
     * @return Root {@link CommandDirectoryDef}.
     */
    public CommandDirectoryDef getRoot() {
        return root;
    }

    /**
     * A builder for a {@link CommandHierarchyDef}.
     */
    @ToString(of = "root")
    public static class Builder {
        private final ReflectionClassProcessor processor = new ReflectionClassProcessor();
        private final CommandDirectoryDef.Builder root = new CommandDirectoryDef.Builder("root").setDescription("root");

        /**
         * Process the classes and add any commands defined through annotations to this builder.
         * Each class must have a no-args constructor.
         *
         * @param classes Classes to process.
         * @return {@code this}, for chaining.
         */
        public Builder processClasses(@NonNull Class<?>... classes) {
            for (Class<?> clazz : classes) {
                doProcessClass(clazz);
            }
            return this;
        }

        private void doProcessClass(Class<?> clazz) {
            final Object instance = ReflectionUtils.createInstanceNoArgs(clazz);
            doProcess(instance);
        }

        /**
         * Process the objects' classes and add any commands defined through annotations to this builder.
         *
         * @param instances Objects whose classes to process.
         * @return {@code this}, for chaining.
         */
        public Builder process(@NonNull Object... instances) {
            for (Object instance : instances) {
                doProcess(instance);
            }
            return this;
        }

        private void doProcess(Object instance) {
            final Map<ParsedPath, List<CommandDef>> pathToCommandDefsMap = processor.processObject(instance);

            // Add the returned commands to the hierarchy.
            for (Entry<ParsedPath, List<CommandDef>> entry : pathToCommandDefsMap.entrySet()) {
                addCommandDefs(entry.getKey(), entry.getValue());
            }
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
         * @return A {@link CommandHierarchyDef} built out of this builder's parameters.
         */
        public CommandHierarchyDef build() {
            final CommandDirectoryDef rootDef = root.build();
            return new CommandHierarchyDef(rootDef);
        }
    }
}

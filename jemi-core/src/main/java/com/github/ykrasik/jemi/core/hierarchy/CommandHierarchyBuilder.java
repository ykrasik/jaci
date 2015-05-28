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

package com.github.ykrasik.jemi.core.hierarchy;

import com.github.ykrasik.jemi.api.Constants;
import com.github.ykrasik.jemi.core.annotation.AnnotationProcessor;
import com.github.ykrasik.jemi.core.command.CommandDef;
import com.github.ykrasik.jemi.core.directory.CommandDirectoryDef;
import com.github.ykrasik.jemi.core.directory.CommandDirectoryDef.Builder;
import com.github.ykrasik.jemi.util.reflection.ReflectionUtils;
import com.github.ykrasik.jemi.util.string.StringUtils;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class CommandHierarchyBuilder {
    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();
    private final Builder root = new Builder();

    /**
     * Process a class and return the commands that were defined in this class with annotations.<br>
     * Class must provide a no-args constructor.<br>
     * Never returns null.
     *
     * @param clazz Class to process.
     * @return The commands and global commands that were defined in the class through annotations.
     */
    // TODO: Wrong JavaDoc
    public CommandHierarchyBuilder processClass(@NonNull Class<?> clazz) {
        final Object instance = ReflectionUtils.createInstanceNoArgs(clazz);
        return processObject(instance);
    }

    /**
     * Process the object and return the commands that were defined in the object's class with annotations.<br>
     * Never returns null.
     *
     * @param instance Object to process.
     * @return The commands and global commands that were defined in the object's class through annotations.
     */
    // TODO: Wrong JavaDoc
    public CommandHierarchyBuilder processObject(@NonNull Object instance) {
        final Map<String, List<CommandDef>> pathToCommandDefsMap = annotationProcessor.processObject(instance);

        // Add the returned commands to the hierarchy.
        for (Entry<String, List<CommandDef>> entry : pathToCommandDefsMap.entrySet()) {
            addCommandDefs(entry.getKey(), entry.getValue());
        }
        return this;
    }

    private void addCommandDefs(String path, List<CommandDef> commandDefs) {
        final String trimmedPath = path.trim();
        // TODO: Use this as a const?
        if ("//".equals(trimmedPath)) {
            throw new IllegalArgumentException(String.format("Invalid path: '%s'", path));
        }

        // Ignore any leading and trailing '/', paths always start from root.
        // TODO: Make sure this doesn't mask '//' or '///' as an error.
        final String pathWithoutDelimiters = StringUtils.removeLeadingAndTrailingDelimiter(path, Constants.PATH_DELIMITER_STRING);
        final List<String> splitPath = Constants.splitByPathDelimiter(pathWithoutDelimiters);

        final Builder builder = getOrCreatePathToDirectory(splitPath);
        builder.addCommandDefs(commandDefs);
    }

    private Builder getOrCreatePathToDirectory(List<String> path) {
        // If an empty path is the only pathElement in the path, this is ok.
        // Allows for paths like '/' and '' to be considered the same.
        if (path.size() == 1 && path.get(0).trim().isEmpty()) {
            return root;
        }

        // Advance along the path, creating directories as necessary.
        Builder dir = root;
        for (String name : path) {
            dir = dir.getOrCreateDirectory(name);
        }
        return dir;
    }

    // TODO: JavaDoc
    CommandDirectoryDef build() {
        return root.build();
    }
}

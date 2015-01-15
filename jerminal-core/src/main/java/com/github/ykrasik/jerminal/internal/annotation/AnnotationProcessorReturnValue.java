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

package com.github.ykrasik.jerminal.internal.annotation;

import com.github.ykrasik.jerminal.api.filesystem.command.Command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Results of processing a class for annotations.
 *
 * @author Yevgeny Krasik
 */
// TODO: Instead of having a return value, consider having the AnnotationProcessor add the commands directory to the file system.
public class AnnotationProcessorReturnValue {
    private final List<Command> globalCommands;
    private final Map<String, List<Command>> commandPaths;

    public AnnotationProcessorReturnValue(List<Command> globalCommands, Map<String, List<Command>> commandPaths) {
        this.globalCommands = Collections.unmodifiableList(Objects.requireNonNull(globalCommands));
        this.commandPaths = Collections.unmodifiableMap(Objects.requireNonNull(commandPaths));
    }

    /**
     * @return List of global commands contained in the processed class.
     */
    public List<Command> getGlobalCommands() {
        return globalCommands;
    }

    /**
     * @return Map of path to List of commands contained in the processed class.
     */
    public Map<String, List<Command>> getCommandPaths() {
        return commandPaths;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnnotationProcessorReturnValue{");
        sb.append("globalCommands=").append(globalCommands);
        sb.append(", commandPaths=").append(commandPaths);
        sb.append('}');
        return sb.toString();
    }
}

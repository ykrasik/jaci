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

package com.github.ykrasik.jerminal.api.display.terminal;

import com.github.ykrasik.jerminal.ShellConstants;

import java.util.List;

/**
 * An implementation for a {@link TerminalGuiController} that requires specialization by subclasses.
 * Handles the common tasks like serialization, but the actual presentation is platform-dependent.
 *
 * @author Yevgeny Krasik
 */
// TODO: This divison between guiController and serializer is inconsistent.
public abstract class DefaultTerminalGuiController implements TerminalGuiController {
    @Override
    public void setWorkingDirectory(List<String> path) {
        final String pathStr = serializePath(path);
        doSetWorkingDirectory(pathStr);
    }

    /**
     * @param path Path to serialize. Path always starts from root.
     * @return Serialized path.
     */
    protected String serializePath(List<String> path) {
        // All paths should start with '/'.
        final StringBuilder sb = new StringBuilder();
        sb.append(ShellConstants.FILE_SYSTEM_DELIMITER);

        // The first element is always the root, skip it.
        for (int i = 1; i < path.size(); i++) {
            final String directory = path.get(i);
            sb.append(directory);
            sb.append(ShellConstants.FILE_SYSTEM_DELIMITER);
        }
        return sb.toString();
    }

    protected abstract void doSetWorkingDirectory(String path);
}

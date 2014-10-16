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

import java.util.List;

/**
 * In charge of manipulating the GUI surrounding the terminal (where text is printed).
 *
 * @author Yevgeny Krasik
 */
public interface TerminalGuiController {
    /**
     * Set the path to the current working directory. Only called when when the working directory changes.
     * The path will always be non-empty and the first element will always be the name of the root directory.
     *
     * @param path New working path.
     */
    // TODO: Provide a more general List<Directory> ?
    void setWorkingDirectory(List<String> path);
}

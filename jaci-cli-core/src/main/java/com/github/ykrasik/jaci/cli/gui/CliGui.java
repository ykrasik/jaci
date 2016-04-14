/******************************************************************************
 * Copyright (c) 2016 Yevgeny Krasik.                                         *
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

package com.github.ykrasik.jaci.cli.gui;

import com.github.ykrasik.jaci.cli.directory.CliDirectory;

/**
 * The GUI controller of a CLI, the GUI being everything around the 'terminal' screen.
 *
 * @author Yevgeny Krasik
 */
// TODO: In the future, this class should receive click events to display contextual information.
public interface CliGui {
    /**
     * Set the 'working directory'.
     * This is a visual detail that simply displays what the current 'working directory' is.
     *
     * @param workingDirectory Working directory to set.
     */
    void setWorkingDirectory(CliDirectory workingDirectory);
}
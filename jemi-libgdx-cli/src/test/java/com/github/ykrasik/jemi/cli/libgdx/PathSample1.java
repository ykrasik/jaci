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

package com.github.ykrasik.jemi.cli.libgdx;

import com.github.ykrasik.jemi.api.*;

/**
 * Class doesn't declare a 'top level path', so all commands will be added under 'root' by default.
 * Contains examples of how to specialize the paths for each command.
 *
 * @author Yevgeny Krasik
 */
public class PathSample1 {
    @CommandPath("new")
    @Command(description = "Simple path specialization.")
    public void specializedPath1(CommandOutput output) {
        output.message("specializedPath1: This command should be located under /new");
    }

    @CommandPath("/new")
    @Command(description = "Path specialization prefixed with a '/' delimiter, has no effect.")
    public void specializedPath2(CommandOutput output) {
        output.message("specializedPath2: This command should be located under /new");
    }

    @CommandPath("new/")
    @Command(description = "Path specialization suffixed with a '/' delimiter, has no effect.")
    public void specializedPath3(CommandOutput output) {
        output.message("specializedPath3: This command should be located under /new");
    }

    @CommandPath("new/path")
    @Command(description = "Path specialization with a delimited path.")
    public void specializedPath4(CommandOutput output) {
        output.message("specializedPath4: This command should be located under /new/path");
    }

    @CommandPath("/new/path")
    @Command(description = "Path specialization with a delimited path prefixed with a '/' delimiter, has no effect.")
    public void specializedPath5(CommandOutput output) {
        output.message("specializedPath5: This command should be located under /new/path");
    }

    @CommandPath("new/path/")
    @Command(description = "Path specialization with a delimited path suffixed with a '/' delimiter, has no effect.")
    public void specializedPath6(CommandOutput output) {
        output.message("specializedPath6: This command should be located under /new/path");
    }
}

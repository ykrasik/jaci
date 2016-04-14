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

package com.github.ykrasik.jaci.commands;

import com.github.ykrasik.jaci.api.Command;
import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.api.CommandPath;

/**
 * Class declares a 'top level path', all commands will be appended to it.
 * Contains examples of how to specialize the paths for each command.
 *
 * @author Yevgeny Krasik
 */
@CommandPath("topLevelPath")
public class PathCommands2 {
    private CommandOutput output;

    @Command(description = "Command without path.")
    public void noPath() {
        output.message("noPath: This command should be located under /topLevelPath");
    }

    @CommandPath("/new")
    @Command(description = "Path specialization prefixed with a '/' delimiter, has no effect.")
    public void specializedPath1() {
        output.message("specializedPath1: This command should be located under /topLevelPath/new");
    }

    @CommandPath("new/")
    @Command(description = "Path specialization suffixed with a '/' delimiter, has no effect.")
    public void specializedPath2() {
        output.message("specializedPath2: This command should be located under /topLevelPath/new");
    }

    @CommandPath("new/path")
    @Command(description = "Path specialization with a delimited path.")
    public void specializedPath3() {
        output.message("specializedPath3: This command should be located under /topLevelPath/new/path");
    }

    @CommandPath("/new/path")
    @Command(description = "Path specialization with a delimited path prefixed with a '/' delimiter, has no effect.")
    public void specializedPath4() {
        output.message("specializedPath4: This command should be located under /topLevelPath/new/path");
    }

    @CommandPath("new/path/")
    @Command(description = "Path specialization with a delimited path suffixed with a '/' delimiter, has no effect.")
    public void specializedPath5() {
        output.message("specializedPath5: This command should be located under /topLevelPath/new/path");
    }
}

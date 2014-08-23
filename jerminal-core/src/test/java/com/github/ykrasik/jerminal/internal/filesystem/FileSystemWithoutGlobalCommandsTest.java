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

package com.github.ykrasik.jerminal.internal.filesystem;

import com.github.ykrasik.jerminal.internal.exception.ParseException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Yevgeny Krasik
 */
public class FileSystemWithoutGlobalCommandsTest extends AbstractFileSystemTest {
    private final String[] commands = { "cmd1", "cmd2", "cmd2cmd", "cmf", "file" };

    @Override
    @Before
    public void setUp() {
        super.setUp();

        add("", commands);
        add("dir1", commands);
        add("dir1/dir2", commands);

        add("dir1/dir3");
        add("dir1/dir3dir");
        add("dir1/dib");
        add("dir1/folder");

        build();
    }

    @Test
    public void testParsePathToCommand() {
        assertPathToCommands("");
        assertPathToCommands("/");

        assertPathToCommands("dir1");
        assertPathToCommands("/dir1");
        assertPathToCommands("dir1/");
        assertPathToCommands("/dir1/");

        assertPathToCommands("dir1/dir2");
        assertPathToCommands("dir1/dir2/");
        assertPathToCommands("/dir1/dir2");
        assertPathToCommands("/dir1/dir2/");
    }

    private void assertPathToCommands(String basePath) {
        final String basePathToUse;
        if (basePath.length() > 1) {
            if (basePath.endsWith("/")) {
                basePathToUse = basePath;
            } else {
                basePathToUse = basePath + '/';
            }
        } else {
            basePathToUse = basePath;
        }
        for (String command : commands) {
            final String path = basePathToUse + command;
            try {
                fileSystem.parsePathToFile(path);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

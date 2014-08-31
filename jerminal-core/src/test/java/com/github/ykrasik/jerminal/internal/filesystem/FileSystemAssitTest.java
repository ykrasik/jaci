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

import org.junit.Before;

/**
 * @author Yevgeny Krasik
 */
public class FileSystemAssitTest extends AbstractFileSystemTest {
    private static final String[] COMMANDS = { "cmd1", "cmd2", "cmd2cmd", "cmf", "file" };

    @Override
    @Before
    public void setUp() {
        super.setUp();

        add("", COMMANDS);
        add("dir1", COMMANDS);
        add("dir1/dir2", COMMANDS);

        add("dir1/dir3");
        add("dir1/dir3dir");
        add("dir1/dib");
        add("dir1/folder");
    }

    // FIXME: Implement.
}

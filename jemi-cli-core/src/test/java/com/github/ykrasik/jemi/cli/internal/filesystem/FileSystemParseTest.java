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

package com.github.ykrasik.jemi.cli.internal.filesystem;

import com.github.ykrasik.jerminal.old.command.Command;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jerminal.old.command.InternalCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Yevgeny Krasik
 */
public class FileSystemParseTest extends AbstractFileSystemTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();

        add("", "cmd1");
        add("dir1", "cmd2");
        add("dir1/dir2", "cmd3");

        add("dir1/dir3", "cmd4");
        add("dir1/dir3dir", "cmd5");
        add("dir1/dib", "cmd6");
        add("folder", "cmd7");

        addGlobalCommands("global");
    }

    @Test
    public void testParsePathToCommand() {
        assertPathToCommand("cmd1", "cmd1");
        assertPathToCommand("/cmd1", "cmd1");
        assertPathToCommandVariations("dir1", "cmd2");
        assertPathToCommandVariations("dir1/dir2", "cmd3");

        // Special characters
        assertPathToCommandVariations(".", "cmd1");
        assertPathToCommandVariations("./dir1", "cmd2");
        assertPathToCommandVariations("dir1/.", "cmd2");
        assertPathToCommandVariations("./dir1/dir2", "cmd3");
        assertPathToCommandVariations("dir1/./dir2", "cmd3");
        assertPathToCommandVariations("dir1/dir2/.", "cmd3");

        assertPathToCommandVariations("dir1/..", "cmd1");
        assertPathToCommandVariations("dir1/dir2/..", "cmd2");
    }

    @Test
    public void testParsePathToDirectory() {
        assertPathToDirectoryVariations("", "dir1");
        assertPathToDirectoryVariations("dir1", "dir2");
        assertPathToDirectoryVariations("dir1", "dir3");
        assertPathToDirectoryVariations("dir1", "dir3dir");
        assertPathToDirectoryVariations("dir1", "dib");
        assertPathToDirectoryVariations("", "folder");

        // Special characters
        assertPathToDirectoryVariations(".", "dir1");
        assertPathToDirectoryVariations("./dir1", "dir2");
        assertPathToDirectoryVariations("dir1/.", "dir3");
        assertPathToDirectoryVariations("./dir1/.", "dir3dir");
        assertPathToDirectoryVariations("././dir1/./.", "dib");
        assertPathToDirectoryVariations(".", "folder");

        assertPathToDirectoryVariations("dir1/..", "dir1");
        assertPathToDirectoryVariations("dir1/dir2/..", "dir2");
    }

    @Test
    public void testParseGlobalCommand() {
        assertPathToCommand("global", "global");

        // Only way of accessing a global command is without any delimiters/special characters.
        assertDoesntExist("/global");
        assertDoesntExist("./global");
        assertDoesntExist("dir1/../global");
    }

    @Test
    public void testGlobalCommandBeforeLocalCommand() {
        // Global commands take precedence over local commands when parsing.
        final String name = "command";

        final Command globalCommand = cmd(name);
        internalFileSystem.addGlobalCommands(globalCommand);

        final Command localCommand = cmd(name);
        fileSystem.addCommands(localCommand);

        try {
            InternalCommand command = internalFileSystem.parsePathToCommand(name);
            assertTrue(command.getCommand() == globalCommand);

            // Adding anything else except the command name makes this no longer viable for a global command.
            command = internalFileSystem.parsePathToCommand('/' + name);
            assertTrue(command.getCommand() == localCommand);

            command = internalFileSystem.parsePathToCommand("./" + name);
            assertTrue(command.getCommand() == localCommand);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // FIXME: Add a currentDirectory test
    // FIXME: Add invalid path test

    private void assertPathToCommandVariations(String basePath, String commandName) {
        assertPathToCommand(basePath + '/' + commandName, commandName);
        assertPathToCommand('/' + basePath + '/' + commandName, commandName);
    }

    private void assertPathToDirectoryVariations(String basePath, String directoryName) {
        assertPathToDirectory(basePath + '/' + directoryName, directoryName);
        assertPathToDirectory(basePath + '/' + directoryName + '/', directoryName);

        if (!basePath.isEmpty()) {
            assertPathToDirectory('/' + basePath + '/' + directoryName, directoryName);
            assertPathToDirectory('/' + basePath + '/' + directoryName + '/', directoryName);
        }
    }
}

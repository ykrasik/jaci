package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieBuilder;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * User: ykrasik
 * Date: 07/08/2014
 * Time: 19:15
 */
@RunWith(MockitoJUnitRunner.class)
public class FileSystemWithoutGlobalCommandsTest extends AbstractFileSystemTest {
    private final String[] commands = { "cmd1", "cmd2", "cmd2cmd", "cmf", "file" };

    @Mock
    private CurrentDirectoryContainer currentDirectoryContainer;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        when(currentDirectoryContainer.getCurrentDirectory()).thenReturn(fileSystem.getRoot());

        add("", commands);
        add("dir1", commands);
        add("dir1/dir2", commands);

        add("dir1/dir3");
        add("dir1/dir3dir");
        add("dir1/dib");
        add("dir1/folder");

        build();

        final ShellDirectory root = this.fileSystem.getRoot();
        final Trie<ShellCommand> globalCommands = new TrieBuilder<ShellCommand>().build();
        this.fileSystem = new ShellFileSystem(root, globalCommands, currentDirectoryContainer);
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

    private void setCurrentDirectory(String... path) {
        final ShellDirectory directory = getDirectory(Arrays.asList(path));
        when(currentDirectoryContainer.getCurrentDirectory()).thenReturn(directory);
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
            final ParseEntryReturnValue returnValue = fileSystem.parsePathToCommand(path);
            assertTrue("Failed parsing path to command: " + path, returnValue.isSuccess());
        }
    }
}

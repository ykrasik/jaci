package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: ykrasik
 * Date: 09/08/2014
 * Time: 23:36
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractFileSystemTest {
    protected ShellFileSystem fileSystem;

    @Before
    public void setUp() {
        this.fileSystem = new ShellFileSystem(root, globalCommands);
    }

    protected void add(String path) {
        add(path, cmd("cmd"));
    }

    protected void add(String path, ShellCommand... commands) {
        fileSystem.add(path, commands);
    }

    protected ShellCommand cmd(String name) {
        final ShellCommand command = mock(ShellCommand.class);
        when(command.getName()).thenReturn(name);
        when(command.isDirectory()).thenReturn(false);
        return command;
    }

    protected ShellDirectory getDirectory(List<String> path) {
        ShellDirectory currentDirectory = fileSystem.getRoot();
        for (String pathElement : path) {
            currentDirectory = getChild(currentDirectory, pathElement);
        }
        return currentDirectory;
    }

    protected ShellDirectory getChild(ShellDirectory directory, String name) {
        final ParseEntryReturnValue returnValue = directory.parseDirectory(name);
        assertTrue("Directory doesn't contain expected child!", returnValue.isSuccess());
        return returnValue.getSuccess().getEntry().getAsDirectory();
    }
}

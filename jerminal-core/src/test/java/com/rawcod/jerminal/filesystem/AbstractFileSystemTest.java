package com.rawcod.jerminal.filesystem;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
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
    protected ShellFileSystemBuilder builder;
    protected ShellFileSystem fileSystem;

    @Before
    public void setUp() {
        this.builder = new ShellFileSystemBuilder();
        this.fileSystem = null;
    }

    protected void build() {
        this.fileSystem = builder.build();
    }

    protected void add(String path) {
        add(path, "cmd");
    }

    protected void add(String path, String... commands) {
        add(path, Arrays.asList(commands));
    }

    protected void add(String path, List<String> commands) {
        final List<ShellCommand> shellCommands = Lists.transform(commands, new Function<String, ShellCommand>() {
            @Override
            public ShellCommand apply(String input) {
                return cmd(input);
            }
        });
        builder.add(path, shellCommands);
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

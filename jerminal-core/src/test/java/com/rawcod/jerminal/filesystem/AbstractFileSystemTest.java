package com.rawcod.jerminal.filesystem;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

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

    @Mock
    protected CurrentDirectoryContainer currentDirectoryContainer;

    @Before
    public void setUp() {
        this.builder = new ShellFileSystemBuilder(currentDirectoryContainer);
        this.fileSystem = null;
    }

    protected void build() {
        this.fileSystem = builder.build();
        when(currentDirectoryContainer.getCurrentDirectory()).thenReturn(fileSystem.getRoot());
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
        try {
            return directory.parseDirectory(name);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setCurrentDirectory(String... path) {
        final ShellDirectory directory = getDirectory(Arrays.asList(path));
        when(currentDirectoryContainer.getCurrentDirectory()).thenReturn(directory);
    }
}

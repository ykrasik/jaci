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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yevgeny Krasik
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
        try {
            return directory.parseDirectory(name);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setCurrentDirectory(String... path) {
        final ShellDirectory directory = getDirectory(Arrays.asList(path));
        fileSystem.setCurrentDirectory(directory);
    }
}

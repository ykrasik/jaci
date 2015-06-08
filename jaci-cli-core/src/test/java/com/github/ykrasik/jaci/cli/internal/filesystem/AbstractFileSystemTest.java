///*
// * Copyright (C) 2014 Yevgeny Krasik
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.github.ykrasik.jaci.cli.internal.filesystem;
//
//import com.github.ykrasik.jerminal.hierarchy.ShellFileSystem;
//import com.github.ykrasik.jerminal.hierarchy.InternalShellFileSystem;
//import com.github.ykrasik.jerminal.old.command.Command;
//import com.github.ykrasik.jaci.cli.exception.ParseException;
//import com.github.ykrasik.jerminal.old.command.InternalCommand;
//import com.github.ykrasik.jemi.core.directory.InternalCommandDirectory;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * @author Yevgeny Krasik
// */
//@RunWith(MockitoJUnitRunner.class)
//public abstract class AbstractFileSystemTest {
//    protected ShellFileSystem fileSystem;
//    protected InternalShellFileSystem internalFileSystem;
//
//    @Before
//    public void setUp() {
//        this.fileSystem = new ShellFileSystem();
//        this.internalFileSystem = null;
//    }
//
//    protected void build() {
//        this.internalFileSystem = new InternalShellFileSystem(fileSystem);
//    }
//
//    protected void addToRoot(String... commandNames) {
//        for (String commandName : commandNames) {
//            fileSystem.addCommands(cmd(commandName));
//        }
//    }
//
//    protected void add(String path, String... commandNames) {
//        for (String commandName : commandNames) {
//            fileSystem.addCommands(path, cmd(commandName));
//        }
//    }
//
//    protected void addGlobalCommands(String... commandNames) {
//        for (String commandName : commandNames) {
//            fileSystem.addGlobalCommands(cmd(commandName));
//        }
//    }
//
//    protected Command cmd(String name) {
//        final Command command = mock(Command.class);
//        when(command.getName()).thenReturn(name);
//        return command;
//    }
//
//    protected void assertPathToCommand(String path, String commandName) {
//        try {
//            final InternalCommand command = internalFileSystem.parsePathToCommand(path);
//            assertEquals(commandName, command.getName());
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected void assertPathToDirectory(String path, String directoryName) {
//        try {
//            final InternalCommandDirectory directory = internalFileSystem.parsePathToDirectory(path);
//            assertEquals(directoryName, directory.getName());
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected void assertGlobalCommand(String commandName) {
//        try {
//            final InternalCommand command = internalFileSystem.parsePathToCommand(commandName);
//            assertEquals(commandName, command.getName());
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected void assertDoesntExist(String path) {
//        try {
//            internalFileSystem.parsePathToCommand(path);
//            fail();
//        } catch (ParseException ignored) { }
//    }
//}

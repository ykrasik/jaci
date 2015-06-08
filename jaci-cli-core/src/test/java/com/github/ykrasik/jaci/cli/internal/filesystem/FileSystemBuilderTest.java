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
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.fail;
//
///**
// * @author Yevgeny Krasik
// */
//public class FileSystemBuilderTest extends AbstractFileSystemTest {
//    @Test
//    public void testBasicPath() {
//        add("dir1/dir2/dir3");
//        assertPath("dir1", "dir2", "dir3");
//    }
//
//    @Test
//    public void testPartiallyExistingPath() {
//        add("dir1");
//        assertPath("dir1");
//
//        // 'dir1' already exists.
//        add("dir1/dir2");
//        assertPath("dir1", "dir2");
//
//        // 'dir1/dir2' already exists.
//        add("dir1/dir2/dir3");
//        assertPath("dir1", "dir2", "dir3");
//    }
//
//    @Test
//    public void testDifferentPaths() {
//        // 2 different paths without anything common.
//        add("dir1/dir2/dir3");
//        add("another1/another2/another3");
//
//        assertPath("dir1", "dir2", "dir3");
//        assertPath("another1", "another2", "another3");
//    }
//
//    @Test
//    public void testPathWithSameNames() {
//        // Nesting the same directory name should be possible.
//        add("dir/dir/dir");
//        assertPath("dir", "dir", "dir");
//    }
//
//    @Test
//    public void testPathWithLeadingDelimiter() {
//        add("/dir1");
//        add("/dir2");
//
//        assertPath("dir1");
//        assertPath("dir2");
//    }
//
//    @Test
//    public void testPathWithTrailingDelimiter() {
//        add("dir1/");
//        add("dir2/");
//
//        assertPath("dir1");
//        assertPath("dir2");
//    }
//
//    @Test
//    public void testPathWithLeadingAndTrailingDelimiter() {
//        add("/dir1/dir2/dir3/");
//        assertPath("dir1", "dir2", "dir3");
//    }
//
//    @Test
//    public void testEmptyPath() {
//        // All these forms represent an empty path.
//        add("", "cmd1");
//        add("/", "cmd2");
//        add("   /", "cmd3");
//        add("/   ", "cmd4");
//        add("   /   ", "cmd5");
//
//        assertPathToCommand("cmd1");
//        assertPathToCommand("cmd2");
//        assertPathToCommand("cmd3");
//        assertPathToCommand("cmd4");
//        assertPathToCommand("cmd5");
//    }
//
//    @Test
//    public void testPathWithSpaces() {
//        // Spaces surrounding a directory name should be ignored.
//        add("    dir1/   dir2   /  dir3    ");
//        assertPath("dir1", "dir2", "dir3");
//    }
//
//    @Test
//    public void testDirNameWithSpaces() {
//        // Spaces that are part of the directory name should be legal.
//        add("dir   ecto  ry/   d i r 2   / d  ir  3  ");
//        assertPath("dir   ecto  ry", "d i r 2", "d  ir  3");
//    }
//
//    @Test
//    public void testIInvalid() {
//        // These are all invalid paths.
//        assertIllegal("//");
//        assertIllegal("    //");
//        assertIllegal("//   ");
//        assertIllegal("/   /");
//        assertIllegal("dir//");
//        assertIllegal("//dir//");
//        assertIllegal("dir1//dir2");
//        assertIllegal("   dir1   //   dir2   //   dir3   ");
//    }
//
//    @Test
//    public void testIllegal() {
//        // These are all illegal due to special characters being used.
//        assertIllegal(".");
//        assertIllegal("/.");
//        assertIllegal("./");
//        assertIllegal("/./");
//
//        assertIllegal("src/main");
//        assertIllegal("/src/main");
//        assertIllegal("../");
//        assertIllegal("/../");
//
//        assertIllegal("./dir1");
//        assertIllegal("/./dir1");
//        assertIllegal("dir1/.");
//        assertIllegal("dir1/./");
//        assertIllegal("dir1/./dir2");
//        assertIllegal("dir1/../dir2");
//        assertIllegal("dir1/dir2/..");
//        assertIllegal("dir1/dir2/../");
//    }
//
//    @Test
//    public void testPathWithDescription() {
//        // It is possible to add descriptions to a directory.
//        add("dir1 : Directory1/  dir2   :   Directory2  /   dir3 : Description with spaces  /  dir4:  ");
//        assertPath("dir1", "dir2", "dir3", "dir4");
//
////        CommandDirectoryDef dir = getChild(fileSystem.getRoot(), "dir1");
////        assertEquals("Directory1", dir.getDescription());
////
////        dir = getChild(dir, "dir2");
////        assertEquals("Directory2", dir.getDescription());
////
////        dir = getChild(dir, "dir3");
////        assertEquals("Description with spaces", dir.getDescription());
////
////        dir = getChild(dir, "dir4");
////        assertEquals("", dir.getDescription());
////
////        // Try again with different descriptions, check that descriptions didn't change.
////        add("dir1:asd/dir2: asd/dir3 : asd/dir4:", "cmd2");
////        assertPath("dir1", "dir2", "dir3", "dir4");
////
////        dir = getChild(fileSystem.getRoot(), "dir1");
////        assertEquals("Directory1", dir.getDescription());
////
////        dir = getChild(dir, "dir2");
////        assertEquals("Directory2", dir.getDescription());
////
////        dir = getChild(dir, "dir3");
////        assertEquals("Description with spaces", dir.getDescription());
////
////        dir = getChild(dir, "dir4");
////        assertEquals("", dir.getDescription());
//    }
//
//    @Test
//    public void testAddToRoot() {
//        // These methods are identical and add the commands to the root.
//        add("/", "cmd1");
//        add("", "cmd2");
//        add("       ", "cmd3");
//        fileSystem.addCommands(cmd("cmd4"));
//
//        assertPathToCommand("cmd1");
//        assertPathToCommand("cmd2");
//        assertPathToCommand("cmd3");
//        assertPathToCommand("cmd4");
//    }
//
//    @Test(expected = ShellException.class)
//    public void testAddCommandTwice() {
//        add("dir", "cmd");
//        assertPathToCommand("dir", "cmd");
//
//        add("dir", "cmd");
//    }
//
//    @Test
//    public void testGlobalCommands() {
//        addGlobalCommands("global1", "global2");
//        assertGlobalCommand("global1");
//        assertGlobalCommand("global2");
//    }
//
//    private void add(String path) {
//        add(path, "cmd");
//    }
//
//    private void assertPath(String... path) {
//        assertPathToCommand(ObjectArrays.concat(path, "cmd"));
//    }
//
//    private void assertPathToCommand(String... path) {
//        final List<String> pathElements = Arrays.asList(path);
//        final List<String> pathToDirectory = pathElements.subList(0, pathElements.size() - 1);
//        final String commandName = pathElements.get(pathElements.size() - 1);
////        final CommandDirectoryDef directory = getDirectory(pathToDirectory);
////        try {
////            directory.getFile(commandName);
////        } catch (ParseException e) {
////            throw new RuntimeException(e);
////        }
//        fail();
//    }
//
//    private InternalCommandDirectory getDirectory(List<String> path) {
////        InternalCommandDirectory currentDirectory = internalFileSystem.getRoot();
////        for (String pathElement : path) {
////            currentDirectory = getChild(currentDirectory, pathElement);
////        }
////        return currentDirectory;
//        throw new RuntimeException();
//    }
//
//    private InternalCommandDirectory getChild(InternalCommandDirectory directory, String name) {
//        return directory.getDirectory(name).get();
//    }
//
//    private void assertIllegal(String path) {
//        try {
//            fileSystem.addCommands(path);
//            fail();
//        } catch (ShellException ignored) { }
//    }
//}

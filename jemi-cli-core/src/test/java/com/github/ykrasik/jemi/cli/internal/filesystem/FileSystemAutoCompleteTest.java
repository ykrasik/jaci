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

import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteType;
import com.google.common.collect.ObjectArrays;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Yevgeny Krasik
 */
public class FileSystemAutoCompleteTest extends AbstractFileSystemTest {
    private static final String[] COMMANDS = { "c", "cmd1", "cmd2", "cmd2cmd", "cmf", "file" };
    private static final String[] GLOBAL_COMMANDS = { "g", "gl", "global", "globalCommand", "globalCommand2" };

    private List<String> expectedFiles;
    private List<String> expectedDirectories;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        add("", COMMANDS);
        add("dir1", COMMANDS);
        add("dir1/dir2", COMMANDS);

        add("dir1/dir3", "cmd");
        add("dir1/dir3dir", "cmd");
        add("dir1/dib", "cmd");
        add("dir1/folder", "cmd");

        add("folder", "cmd");

        addGlobalCommands(GLOBAL_COMMANDS);

        this.expectedFiles = Collections.emptyList();
        this.expectedDirectories = Collections.emptyList();
    }

    // FIXME: Add current directory test

    @Test
    public void testAutoCompletePathEmptyPrefix() {
        // All commands and directories are possible with an empty prefix.
        expectFiles(ObjectArrays.concat(COMMANDS, GLOBAL_COMMANDS, String.class));
        expectDirectories("dir1", "folder");
        assertAutoCompletePath("", "");

        // Global commands no longer possible.
        expectFiles(COMMANDS);
        expectDirectories("dir2", "dir3", "dir3dir", "dib", "folder");
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/", "");

        expectDirectories();
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/", "");
    }

    @Test
    public void testAutoCompletePathPrefix1() {
        expectFiles("c", "cmd1", "cmd2", "cmd2cmd", "cmf");
        assertAutoCompletePathVariations("c");
    }

    @Test
    public void testAutoCompletePathPrefix2() {
        expectFiles("cmd1", "cmd2", "cmd2cmd", "cmf");
        assertAutoCompletePathVariations("cm");
    }

    @Test
    public void testAutoCompletePathPrefix3() {
        expectFiles("cmf");
        assertAutoCompletePathVariations("cmf");
    }

    @Test
    public void testAutoCompletePathPrefix4() {
        expectFiles("cmd1", "cmd2", "cmd2cmd");
        assertAutoCompletePathVariations("cmd");
    }

    @Test
    public void testAutoCompletePathPrefix5() {
        expectFiles("cmd1");
        assertAutoCompletePathVariations("cmd1");
    }

    @Test
    public void testAutoCompletePathPrefix6() {
        expectFiles("cmd2", "cmd2cmd");
        assertAutoCompletePathVariations("cmd2");
    }

    @Test
    public void testAutoCompletePathPrefix7() {
        expectFiles("cmd2cmd");
        assertAutoCompletePathVariations("cmd2c");
    }

    @Test
    public void testAutoCompletePathPrefix8() {
        expectFiles("cmd2cmd");
        assertAutoCompletePathVariations("cmd2cmd");
    }

    @Test
    public void testAutoCompletePathPrefix9() {
        expectFiles("file");
        expectDirectories("folder");
        assertAutoCompletePathWithAndWithoutDelimiter("f", "f");
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/f", "f");

        expectDirectories();
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/f", "f");
    }

    @Test
    public void testAutoCompletePathPrefix10() {
        expectDirectories("folder");
        assertAutoCompletePathWithAndWithoutDelimiter("fo", "fo");
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/fo", "fo");

        expectDirectories();
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/fo", "fo");
    }

    @Test
    public void testAutoCompletePathPrefix11() {
        expectFiles("file");
        assertAutoCompletePathVariations("fi");
    }

    @Test
    public void testAutoCompletePathPrefix12() {
        expectDirectories("dir1");
        assertAutoCompletePathWithAndWithoutDelimiter("d", "d");

        expectDirectories("dir2", "dir3", "dir3dir", "dib");
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/d", "d");

        expectDirectories();
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/d", "d");
    }

    @Test
    public void testAutoCompletePathInvalidPrefix() {
        // These are all invalid prefixes.
        assertAutoCompletePathVariations("cmd3");
        assertAutoCompletePathVariations("cmd2d");
        assertAutoCompletePathVariations("cmd2cmd1");
        assertAutoCompletePathVariations("e");
    }

    @Test
    public void testInvalidPath() {
        assertInvalidPath("dir/");
        assertInvalidPath("dir1/dir/");
        assertInvalidPath("dir1/dir2/d/");
    }

    @Test
    public void testAutoCompleteDirectory() {
        // FIXME: Implement.
    }

    private void expectFiles(String... files) {
        expectedFiles = Arrays.asList(files);
    }

    private void expectDirectories(String... directories) {
        expectedDirectories = Arrays.asList(directories);
    }

    private void assertAutoCompletePathVariations(String prefix) {
        assertAutoCompletePathWithAndWithoutDelimiter(prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("./" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/../" + prefix, prefix);

        assertAutoCompletePathWithAndWithoutDelimiter("dir1/" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("./dir1/" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/./" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/../" + prefix, prefix);

        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("./dir1/dir2/" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/./dir2/" + prefix, prefix);
        assertAutoCompletePathWithAndWithoutDelimiter("dir1/dir2/./" + prefix, prefix);
    }

    private void assertAutoCompletePathWithAndWithoutDelimiter(String path, String prefix) {
        assertAutoCompletePath(path, prefix);
        assertAutoCompletePath('/' + path, prefix);
    }

    private void assertAutoCompletePath(String rawPath, String expectedPrefix) {
        try {
            final AutoCompleteReturnValue returnValue = internalFileSystem.autoCompletePath(rawPath);
            doAssertAutoComplete(returnValue, expectedPrefix);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertAutoCompleteDirectory(String rawPath, String expectedPrefix) {
        try {
            final AutoCompleteReturnValue returnValue = internalFileSystem.autoCompletePathToDirectory(rawPath);
            doAssertAutoComplete(returnValue, expectedPrefix);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void doAssertAutoComplete(AutoCompleteReturnValue returnValue, String expectedPrefix) {
        assertEquals(expectedPrefix, returnValue.getPrefix());

        final Map<String, AutoCompleteType> possibilities = returnValue.getPossibilities().toMap();
        assertAutoCompleteType(possibilities, expectedFiles, AutoCompleteType.COMMAND);
        assertAutoCompleteType(possibilities, expectedDirectories, AutoCompleteType.DIRECTORY);

        assertEquals("Not all expected autoComplete values are present", expectedFiles.size() + expectedDirectories.size(), possibilities.size());
    }

    private void assertAutoCompleteType(Map<String, AutoCompleteType> possibilities,
                                        List<String> expectedEntries,
                                        AutoCompleteType expectedType) {
        for (String expectedEntry : expectedEntries) {
            assertEquals(expectedType, possibilities.get(expectedEntry));
        }
    }

    private void assertInvalidPath(String path) {
        try {
            internalFileSystem.autoCompletePath(path);
            fail();
        } catch (ParseException ignored) { }
    }
}

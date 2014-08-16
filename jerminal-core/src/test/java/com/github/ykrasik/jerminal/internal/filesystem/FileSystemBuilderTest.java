package com.github.ykrasik.jerminal.internal.filesystem;

import com.google.common.collect.ObjectArrays;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: ykrasik
 * Date: 09/08/2014
 * Time: 21:08
 */
public class FileSystemBuilderTest extends AbstractFileSystemTest {
    @Test
    public void testBasicPath() {
        add("dir1/dir2/dir3");
        assertPath("dir1", "dir2", "dir3");
    }

    @Test
    public void testPartiallyExistingPath() {
        add("dir1");
        assertPath("dir1");

        add("dir1/dir2");
        assertPath("dir1", "dir2");

        add("dir1/dir2/dir3");
        assertPath("dir1", "dir2", "dir3");
    }

    @Test
    public void testDifferentPaths() {
        add("dir1/dir2/dir3");
        assertPath("dir1", "dir2", "dir3");

        add("another1/another2/another3");
        assertPath("another1", "another2", "another3");
    }

    @Test
    public void testPathWithSameNames() {
        add("dir/dir/dir");
        assertPath("dir", "dir", "dir");
    }

    @Test
    public void testSamePathTwice() {
        builder.add("dir1/dir2");
        assertPathToCommand("dir1", "dir2", null);

        builder.add("dir1/dir2");
        assertPathToCommand("dir1", "dir2", null);
    }

    @Test
    public void testPathWithLeadingDelimiter() {
        add("/dir1");
        assertPath("dir1");

        add("/dir2");
        assertPath("dir2");
    }

    @Test
    public void testPathWithTrailingDelimiter() {
        add("dir1/");
        assertPath("dir1");

        add("dir2/");
        assertPath("dir2");
    }

    @Test
    public void testPathWithLeadingAndTrailingDelimiter() {
        add("/dir1/dir2/dir3/");
        assertPath("dir1", "dir2", "dir3");
    }

    @Test
    public void testEmptyPath() {
        add("", "cmd1");
        assertPathToCommand("cmd1");

        add("/", "cmd2");
        assertPathToCommand("cmd2");

        add("   /", "cmd3");
        assertPathToCommand("cmd3");

        add("/   ", "cmd4");
        assertPathToCommand("cmd4");

        add("   /   ", "cmd5");
        assertPathToCommand("cmd5");
    }

    @Test
    public void testPathWithSpaces() {
        add("    dir1/   dir2   /  dir3    ");
        assertPath("dir1", "dir2", "dir3");
    }

    @Test
    public void testDirNameWithSpaces() {
        add("dir   ecto  ry/   d i r 2   / d  ir  3  ");
        assertPath("dir   ecto  ry", "d i r 2", "d  ir  3");
    }

    @Test
    public void testIllegalPath() {
        assertIllegal("//");
        assertIllegal("    //");
        assertIllegal("//   ");
        assertIllegal("/   /");
        assertIllegal("dir//");
        assertIllegal("//dir//");
        assertIllegal("dir1//dir2");
        assertIllegal("   dir1   //   dir2   //   dir3   ");
    }

    @Test
    public void testSpecialCharacters() {
        add("./dir1", "cmd1");
        assertPathToCommand("dir1", "cmd1");

        add("/./dir1", "cmd2");
        assertPathToCommand("dir1", "cmd2");

        add("dir1/.", "cmd3");
        assertPathToCommand("dir1", "cmd3");

        add("dir1/./", "cmd4");
        assertPathToCommand("dir1", "cmd4");

        add("dir1/./dir2", "cmd5");
        assertPathToCommand("dir1", "dir2", "cmd5");

        add("dir1/../dir2", "cmd6");
        assertPathToCommand("dir2", "cmd6");

        add("dir1/dir2/..", "cmd7");
        assertPathToCommand("dir1", "cmd7");

        add("dir1/dir2/../", "cmd8");
        assertPathToCommand("dir1", "cmd8");
    }

    @Test
    public void testIllegalRootParent() {
        assertIllegal("..");
        assertIllegal("/..");
        assertIllegal("../");
        assertIllegal("/../");
    }

    @Test
    public void testPathWithDescription() {
        add("dir1 : Directory1/  dir2   :   Directory2  /   dir3 : Description with spaces  /  dir4:  ");
        assertPath("dir1", "dir2", "dir3", "dir4");

        ShellDirectory dir = getChild(fileSystem.getRoot(), "dir1");
        assertEquals("Directory1", dir.getDescription());

        dir = getChild(dir, "dir2");
        assertEquals("Directory2", dir.getDescription());

        dir = getChild(dir, "dir3");
        assertEquals("Description with spaces", dir.getDescription());

        dir = getChild(dir, "dir4");
        assertEquals("", dir.getDescription());

        // Try again with different descriptions, check that descriptions didn't change.
        add("dir1:asd/dir2: asd/dir3 : asd/dir4:", "cmd2");
        assertPath("dir1", "dir2", "dir3", "dir4");

        dir = getChild(fileSystem.getRoot(), "dir1");
        assertEquals("Directory1", dir.getDescription());

        dir = getChild(dir, "dir2");
        assertEquals("Directory2", dir.getDescription());

        dir = getChild(dir, "dir3");
        assertEquals("Description with spaces", dir.getDescription());

        dir = getChild(dir, "dir4");
        assertEquals("", dir.getDescription());
    }

    @Test
    public void testAddToRoot() {
        // These methods are identical.
        add("/", "cmd1");
        add("", "cmd2");
        add("       ", "cmd3");
        builder.add(cmd("cmd4"));

        assertPathToCommand("cmd1");
        assertPathToCommand("cmd2");
        assertPathToCommand("cmd3");
        assertPathToCommand("cmd4");
    }

    @Test(expected = IllegalStateException.class)
    public void testAddCommandTwice() {
        add("dir", "cmd");
        assertPathToCommand("dir", "cmd");

        add("dir", "cmd");
    }

    private void assertPath(String... path) {
        assertPathToCommand(ObjectArrays.concat(path, "cmd"));
    }

    private void assertPathToCommand(String... path) {
        final List<String> pathElements = Arrays.asList(path);
        final List<String> pathToDirectory = pathElements.subList(0, pathElements.size() - 1);
        final String commandName = pathElements.get(pathElements.size() - 1);
        final ShellDirectory directory = getDirectory(pathToDirectory);
        if (commandName != null) {
            try {
                directory.parseCommand(commandName);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void assertIllegal(String path) {
        try {
            builder.add(path);
            fail();
        } catch (ShellException ignored) { }
    }
}

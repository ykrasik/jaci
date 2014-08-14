package com.rawcod.jerminal.filesystem;

import com.rawcod.jerminal.exception.ParseException;
import org.junit.Before;
import org.junit.Test;

/**
 * User: ykrasik
 * Date: 07/08/2014
 * Time: 19:15
 */
public class FileSystemWithoutGlobalCommandsTest extends AbstractFileSystemTest {
    private final String[] commands = { "cmd1", "cmd2", "cmd2cmd", "cmf", "file" };

    @Override
    @Before
    public void setUp() {
        super.setUp();

        add("", commands);
        add("dir1", commands);
        add("dir1/dir2", commands);

        add("dir1/dir3");
        add("dir1/dir3dir");
        add("dir1/dib");
        add("dir1/folder");

        build();
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
            try {
                fileSystem.parsePathToCommand(path);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

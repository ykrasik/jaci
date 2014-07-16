package com.rawcod.jerminal;

import com.rawcod.jerminal.shell.entry.directory.ShellTree;
import org.junit.Assert;

import java.util.List;

/**
* User: ykrasik
* Date: 10/01/14
*/
public class TestTerminal implements Terminal {
    private String message;
    private String errorMessage;
    private String commandReturnMessage;
    private String usage;
    private List<String> suggestions;
    private String commandLine;
    private int commandLineCursor = -1;
    private List<String> currentPath;
    private ShellTree shellTree;

    private boolean expectedSuccess;
    private String[] expectedSuggestions;
    private String expectedCommandLine;

    @Override
    public void displayMessage(String message) {
        this.message = message;
    }

    @Override
    public void displayCommandReturnMessage(String message) {
        this.commandReturnMessage = message;
    }

    @Override
    public void displayError(String error) {
        this.errorMessage = error;
    }

    @Override
    public void displayUsage(String usage) {
        this.usage = usage;
    }

    @Override
    public void displaySuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public void displayShellTree(ShellTree shellTree) {
        this.shellTree = shellTree;
    }

    @Override
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public void setCommandLineCursor(int index) {
        this.commandLineCursor = index;
    }

    @Override
    public void setCurrentPath(List<String> path) {
        this.currentPath = path;
    }

    public TestTerminal expectSuccess() {
        this.expectedSuccess = true;
        return this;
    }

    public TestTerminal expectError() {
        this.expectedSuccess = false;
        return this;
    }

    public TestTerminal expectSuggestions(String... expectedSuggestions) {
        this.expectedSuggestions = expectedSuggestions;
        return this;
    }

    public TestTerminal expectNoSuggestions() {
        this.expectedSuggestions = null;
        return this;
    }

    public TestTerminal expectCommandLine(String expectedCommandLine) {
        this.expectedCommandLine = expectedCommandLine;
        return this;
    }

    public TestTerminal expectCommandLineNotChanged() {
        this.expectedCommandLine = null;
        return this;
    }

    public void assertExpected() {
        if (expectedSuccess) {
            Assert.assertNull("Unexpected error received:", errorMessage);
        } else {
            Assert.assertNotNull("Expected to get an error:", errorMessage);
        }

        if (expectedSuggestions == null) {
            Assert.assertNull("Unexpected suggestions received:", suggestions);
        } else {
            Assert.assertNotNull("No suggestions received", suggestions);
            for (String suggestion : expectedSuggestions) {
                Assert.assertTrue("Expected suggestion missing: " + suggestion,
                                  suggestions.contains(suggestion));
            }
            Assert.assertEquals("Offered more suggestions then expected",
                                expectedSuggestions.length, suggestions.size());
        }

        if (expectedCommandLine == null) {
            Assert.assertNull("Unexpected commandLine change:", commandLine);
            Assert.assertEquals("Unexpected commandLine cursor change", -1, commandLineCursor);
        } else {
            Assert.assertNotNull("CommandLine wasn't changed as expected:", commandLine);
            Assert.assertEquals("Unexpected commandLine", expectedCommandLine, commandLine);
            Assert.assertEquals("CommandLine cursor not at expected position",
                                expectedCommandLine.length(), commandLineCursor);
        }
    }
}

package com.rawcod.jerminal.directory;

import com.rawcod.jerminal.TestTerminal;
import com.rawcod.jerminal.Shell;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * User: ykrasik
 * Date: 10/01/14
 */
public class DirectorySpacesTest {
    private TestTerminal terminal;
    private Shell shell;

    @Before
    public void setup() {
        final ShellManager manager = new ShellManager();
        manager.addEntries(
            new ShellDirectory("d").addEntries(
                new ShellDirectory("multiplePossible1"),
                new ShellDirectory("multiplePossible2")
            ),
            new ShellDirectory("dirStart").addEntry(
                new ShellDirectory("dir1").addEntry(
                    new ShellDirectory("dir2").addEntry(
                        new ShellDirectory("dirEnd")
                    )
                )
            )
        );

        terminal = new TestTerminal();
        shell = new Shell(manager, terminal, 3);

        terminal.expectSuccess();
    }

    @Test
    public void emptyCommandLine() {
        // Empty command line, all suggestions
        shell.autoComplete("");
        terminal
            .expectSuggestions("d", "dirStart")
            .expectCommandLine("d");
        terminal.assertExpected();
    }

    @Test
    public void spacesCommandLine() {
        // Spaces are considered the same as an empty commandLine
        shell.autoComplete("                                       ");
        terminal
            .expectSuggestions("d", "dirStart")
            .expectCommandLine("d");
        terminal.assertExpected();
    }

    @Test
    public void leadingSpacesIgnoredMultiplePossible() {
        // Leading spaces are ignored
        // Multiple possibilities with this prefix
        shell.autoComplete("                d");
        terminal
            .expectSuggestions("d", "dirStart")
            .expectCommandLine("d");
        terminal.assertExpected();
    }

    @Test
    public void leadingTrailingSpacesIgnoredMultiplePossibleForceDecision() {
        // Leading spaces are ignored
        // Multiple possibilities with this prefix
        // Adding spaces afterwards forces the auto-complete decision to be made
        shell.autoComplete("             d                ");
        terminal
            .expectSuggestions("multiplePossible1", "multiplePossible2")
            .expectCommandLine("d multiplePossible");
        terminal.assertExpected();
    }

    @Test
    public void leadingSpacesIgnoredSinglePossiblePartiallyTyped() {
        // Leading spaces are ignored
        // Single possibility for further auto-completion, so it's taken
        shell.autoComplete("            di");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart ");
        terminal.assertExpected();
    }

    @Test
    public void leadingTrailingSpacesIgnoredSinglePossiblePartiallyTypedForceDecision() {
        // Leading spaces are ignored
        // Adding spaces afterwards forces the auto-complete decision to be made
        // Trying to force the decision made it invalid
        shell.autoComplete("            di          ");
        terminal
            .expectError()
            .expectSuggestions("dirStart")
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void leadingSpacesIgnoredSinglePossibleFullyTyped() {
        // Leading spaces are ignored
        // Single possibility for further auto-completion, so it's taken
        shell.autoComplete("            dirStart");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart ");
        terminal.assertExpected();
    }

    @Test
    public void leadingTrailingSpacesIgnoredSinglePossibleFullyTypedForceDecision() {
        // Leading spaces are ignored
        // Single possibility for further auto-completion, so it's taken
        // Adding spaces afterwards forces the auto-complete decision to be made
        shell.autoComplete("            dirStart            ");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart dir1 ");
        terminal.assertExpected();
    }

    @Test
    public void leadingSpacesIgnoredInvalidPrefix() {
        // Leading spaces are ignored
        // No possibilities with this prefix
        shell.autoComplete("            s");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void leadingTrailingSpacesIgnoredInvalidPrefixTryForce() {
        // Leading spaces are ignored
        // No possibilities with this prefix, but we try to force it by adding spaces afterwards
        shell.autoComplete("            s       ");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void trailingSpacesIgnoredInvalidPrefix() {
        // Trailing spaces should be ignored
        // No possibilities with this prefix, but we try to force it by adding spaces afterwards
        shell.autoComplete("s                  ");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void trailingSpacesIgnored() {
        // Trailing spaces should be ignored
        // Only 1 possible suggestion so it's auto-completed
        shell.autoComplete("dirStart                  ");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart dir1 ");
        terminal.assertExpected();
    }

    @Test
    public void inBetweenSpacesIgnored() {
        // Spaces in between words should be ignored
        shell.autoComplete("dirStart                  dir1");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart dir1 ");
        terminal.assertExpected();
    }

    @Test
    public void lotsOfSpacesEverywhereSinglePossibleFromBlank() {
        // Ignore all these spaces
        // Single possible suggestion so it's auto-completed
        shell.autoComplete("            dirStart           dir1          dir2       ");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart dir1 dir2 dirEnd ");
        terminal.assertExpected();
    }

    @Test
    public void lotsOfSpacesEverywhereSinglePossiblePartiallyTyped() {
        // Ignore all these spaces
        // Single possible suggestion so it's auto-completed
        shell.autoComplete("            dirStart           dir1          dir2       d");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("dirStart dir1 dir2 dirEnd ");
        terminal.assertExpected();
    }

    @Test
    public void lotsOfSpacesEverywhereSinglePossiblePartiallyTypedForceDecision() {
        // Ignore all these spaces
        // Trying to force the decision made the prefix invalid
        shell.autoComplete("            dirStart           dir1          dir2       d          ");
        terminal
            .expectError()
            .expectSuggestions("dirEnd")
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void lotsOfSpacesEverywhereInvalidPrefix() {
        // Ignore all these spaces
        // No possible entries with this prefix
        shell.autoComplete("            dirStart           dir1          dir2       c");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void lotsOfSpacesEverywhereNonePossibleTryForce() {
        // Ignore all these spaces
        // No possible entries with this prefix and we try to force it by adding spaces after
        shell.autoComplete("            dirStart           dir1          dir2        c         ");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }
}

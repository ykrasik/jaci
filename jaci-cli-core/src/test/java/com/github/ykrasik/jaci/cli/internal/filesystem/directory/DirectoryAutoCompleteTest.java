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

package com.github.ykrasik.jaci.cli.internal.filesystem.directory;

/**
 * @author Yevgeny Krasik
 */
//public class DirectoryAutoCompleteTest {
//    private TestTerminal terminal;
//    private Shell shell;
//
//    @Before
//    public void setup() {
//        final ShellManager manager = new ShellManager();
//        manager.addEntries(
//            new CommandDirectoryImpl("d").addEntries(
//                new CommandDirectoryImpl("1possible"),
//                new CommandDirectoryImpl("2possible")
//            ),
//            new CommandDirectoryImpl("dir").addEntry(
//                new CommandDirectoryImpl("singlePossible")),
//            new CommandDirectoryImpl("dir1").addEntry(
//                new CommandDirectoryImpl("singlePossible")
//            ),
//            new CommandDirectoryImpl("dir2").addEntry(
//                new CommandDirectoryImpl("singlePossible")
//            ),
//            new CommandDirectoryImpl("directory").addEntry(
//                new CommandDirectoryImpl("singlePossible")
//            ),
//            new CommandDirectoryImpl("emptyDirectory")
//        );
//
//        terminal = new TestTerminal();
//        shell = new Shell(manager, terminal, 3);
//
//        terminal.expectSuccess();
//    }
//
//    @Test
//    public void singleLetterMultiplePossible() {
//        // Multiple possibilities with this prefix
//        shell.autoComplete("d");
//        terminal
//            .expectSuggestions("d", "dir", "dir1", "dir2", "directory")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleLetterForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Multiple possibilities for further auto-completion
//        shell.autoComplete("d ");
//        terminal
//            .expectSuggestions("1possible", "2possible")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void twoLettersMultiplePossible() {
//        // Multiple possibilities with this prefix
//        shell.autoComplete("di");
//        terminal
//            .expectSuggestions("dir", "dir1", "dir2", "directory")
//            .expectCommandLine("dir");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void twoLettersMultiplePossibleForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Adding the space made the prefix invalid
//        shell.autoComplete("di ");
//        terminal
//            .expectError()
//            .expectSuggestions("dir", "dir1", "dir2", "directory")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void threeLettersMultiplePossible() {
//        // Multiple possibilities with this prefix
//        shell.autoComplete("dir");
//        terminal
//            .expectSuggestions("dir", "dir1", "dir2", "directory")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void threeLettersForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("dir ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("dir singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix1() {
//        // Single possibility with this prefix
//        shell.autoComplete("dir1");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("dir1 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix1ForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("dir1 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("dir1 singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix2() {
//        // Single possibility with this prefix
//        shell.autoComplete("dir2");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("dir2 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix2ForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("dir2 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("dir2 singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void invalidPrefix() {
//        // No such prefix
//        shell.autoComplete("dir3");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void invalidPrefixForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // The prefix is still invalid though
//        shell.autoComplete("dir3 ");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singlePossiblePrefix() {
//        // Single possibility with this prefix
//        shell.autoComplete("dire");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("directory ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singlePossiblePrefixInvalidForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Adding the space made the prefix invalid
//        shell.autoComplete("dire ");
//        terminal
//            .expectError()
//            .expectSuggestions("directory")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singlePossiblePrefixInvalidForceDecisionAgain() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Adding the space made the prefix invalid
//        shell.autoComplete("dire s");
//        terminal
//            .expectError()
//            .expectSuggestions("directory")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singlePossibleFullyTypedPrefix() {
//        // Single possibility with this prefix
//        shell.autoComplete("directory");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("directory ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void emptyDirectory() {
//        // Directory is empty
//        shell.autoComplete("emptyDirectory ");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//}

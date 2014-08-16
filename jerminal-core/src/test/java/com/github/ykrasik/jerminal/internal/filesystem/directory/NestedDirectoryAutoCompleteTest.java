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

package com.github.ykrasik.jerminal.internal.filesystem.directory;

/**
 * @author Yevgeny Krasik
 */
//public class NestedDirectoryAutoCompleteTest {
//    private TestTerminal terminal;
//    private Shell shell;
//
//    @Before
//    public void setup() {
//        final ShellManager manager = new ShellManager();
//        manager.addEntry(
//            new ShellDirectoryImpl("nested").addEntries(
//                new ShellDirectoryImpl("d").addEntries(
//                    new ShellDirectoryImpl("1possible"),
//                    new ShellDirectoryImpl("2possible")
//                ),
//                new ShellDirectoryImpl("dir").addEntry(
//                    new ShellDirectoryImpl("singlePossible")
//                ),
//                new ShellDirectoryImpl("dir1").addEntry(
//                    new ShellDirectoryImpl("singlePossible")
//                ),
//                new ShellDirectoryImpl("dir2").addEntry(
//                    new ShellDirectoryImpl("singlePossible")
//                ),
//                new ShellDirectoryImpl("directory").addEntries(
//                    new ShellDirectoryImpl("singlePossible").addEntries(
//                        new ShellDirectoryImpl("multiplePossible1").addEntry(
//                            new ShellDirectoryImpl("singlePossible")
//                        ),
//                        new ShellDirectoryImpl("multiplePossible2").addEntry(
//                            new ShellDirectoryImpl("singlePossible")
//                        )
//                    )
//                )
//            )
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
//        shell.autoComplete("nested d");
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
//        shell.autoComplete("nested d ");
//        terminal
//            .expectSuggestions("1possible", "2possible")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void twoLettersMultiplePossible() {
//        // Multiple possibilities with this prefix
//        shell.autoComplete("nested di");
//        terminal
//            .expectSuggestions("dir", "dir1", "dir2", "directory")
//            .expectCommandLine("nested dir");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void twoLettersMultiplePossibleForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Adding the space made the prefix invalid
//        shell.autoComplete("nested di ");
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
//        shell.autoComplete("nested dir");
//        terminal
//            .expectSuggestions("dir", "dir1", "dir2", "directory")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void threeLettersForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility for furthuto-completion, so it's taken
//        shell.autoComplete("nested dir ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested dir singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix1() {
//        // Single possibility with this prefix
//        shell.autoComplete("nested dir1");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested dir1 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix1ForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("nested dir1 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested dir1 singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix2() {
//        // Single possibility with this prefix
//        shell.autoComplete("nested dir2");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested dir2 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void uniquePrefix2ForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("nested dir2 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested dir2 singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void invalidPrefix() {
//        // No such prefix
//        shell.autoComplete("nested dir3");
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
//        shell.autoComplete("nested dir3 ");
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
//        shell.autoComplete("nested dire");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singlePossiblePrefixInvalidForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility with this prefix
//        // Trying to force the decision made the prefix invalid
//        shell.autoComplete("nested dire ");
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
//        // Single possibility with this prefix
//        // Trying to force the decision made the prefix invalid
//        shell.autoComplete("nested dire s");
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
//        shell.autoComplete("nested directory");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossibleBlankPrefix() {
//        // Single possibility with this prefix
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("nested directory ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossibleInvalidPrefix() {
//        // Invalid prefix
//        shell.autoComplete("nested directory m");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossibleInvalidPrefixForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Still an invalid prefix
//        shell.autoComplete("nested directory m ");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossiblePartiallyTypedPrefix() {
//        // Single possibility with this prefix
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("nested directory s");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossibleFullyTypedPrefix() {
//        // Single possibility with this prefix
//        // Single possibility for further auto-completion, so it's taken
//        shell.autoComplete("nested directory singlePossible");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedMultiplePossibleBlankPrefix() {
//        // Multiple possibilities with this prefix
//        // Auto-complete as much as possible
//        shell.autoComplete("nested directory singlePossible ");
//        terminal
//            .expectSuggestions("multiplePossible1", "multiplePossible2")
//            .expectCommandLine("nested directory singlePossible multiplePossible");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedMultiplePossiblePartiallyTypedPrefix() {
//        // Multiple possibilities with this prefix
//        // Auto-complete as much as possible
//        shell.autoComplete("nested directory singlePossible m");
//        terminal
//            .expectSuggestions("multiplePossible1", "multiplePossible2")
//            .expectCommandLine("nested directory singlePossible multiplePossible");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedMultiplePossibleInvalidPrefix() {
//        // Invalid prefix
//        shell.autoComplete("nested directory singlePossible s");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossiblePrefix1() {
//        // Single possibility with this prefix
//        shell.autoComplete("nested directory singlePossible multiplePossible1");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible multiplePossible1 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossiblePrefix1ForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility with this prefix
//        shell.autoComplete("nested directory singlePossible multiplePossible1 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible multiplePossible1 singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossiblePrefix2() {
//        // Single possibility with this prefix
//        shell.autoComplete("nested directory singlePossible multiplePossible2");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible multiplePossible2 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedSinglePossiblePrefix2ForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Single possibility with this prefix
//        shell.autoComplete("nested directory singlePossible multiplePossible2 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("nested directory singlePossible multiplePossible2 singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedDeepSinglePossibleInvalidPrefix() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Still an invalid prefix
//        shell.autoComplete("nested directory singlePossible1 multiplePossible1");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void nestedDeepSinglePossibleInvalidPrefixForceDecision() {
//        // Adding a space afterwards forces the auto-complete decision to be made
//        // Still an invalid prefix
//        shell.autoComplete("nested directory singlePossible1 multiplePossible1 ");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//}

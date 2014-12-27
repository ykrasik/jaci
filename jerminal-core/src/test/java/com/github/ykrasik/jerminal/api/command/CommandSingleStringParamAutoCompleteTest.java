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

package com.github.ykrasik.jerminal.api.command;

/**
 * @author Yevgeny Krasik
 */
// TODO: Do something with this file
//public class CommandSingleStringParamAutoCompleteTest {
//    private TestTerminal terminal;
//    private Shell shell;
//
//    private ShellManager manager;
//
//    @Before
//    public void setup() {
//        manager = new ShellManager();
//
//        terminal = new TestTerminal();
//        shell = new Shell(manager, terminal, 3);
//
//        terminal.expectSuccess();
//    }
//
//    @Test
//    public void noParams() {
//        setParams();
//        shell.autoComplete("cmd ");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void noParamsForce() {
//        setParams();
//        shell.autoComplete("cmd s");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamNoPossibleValuesBlank() {
//        setParams(new StringCommandParam("param"));
//        shell.autoComplete("cmd ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamNoPossibleValuesSomeValue() {
//        setParams(new StringCommandParam("param"));
//        shell.autoComplete("cmd someValue");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd someValue ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamNoPossibleValuesSomeValueForce() {
//        setParams(new StringCommandParam("param"));
//        shell.autoComplete("cmd someValue ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamSinglePossibleValuesBlank() {
//        setParams(new StringCommandParam("param", "singlePossible"));
//        shell.autoComplete("cmd ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamSinglePossibleValuesPartiallyTyped() {
//        setParams(new StringCommandParam("param", "singlePossible"));
//        shell.autoComplete("cmd s");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamSinglePossibleValuesPartiallyTypedForceDecision() {
//        setParams(new StringCommandParam("param", "singlePossible"));
//        shell.autoComplete("cmd s ");
//        terminal
//            .expectError()
//            .expectSuggestions("singlePossible")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamSinglePossibleValuesFullyTyped() {
//        setParams(new StringCommandParam("param", "singlePossible"));
//        shell.autoComplete("cmd singlePossible");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamSinglePossibleValuesNoMoreArgs() {
//        // Spaces after last param are ignored
//        setParams(new StringCommandParam("param", "singlePossible"));
//        shell.autoComplete("cmd singlePossible             ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd singlePossible ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamSinglePossibleValuesNoMoreArgsTyped() {
//        setParams(new StringCommandParam("param", "singlePossible"));
//        shell.autoComplete("cmd singlePossible s");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesBlank() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd ");
//        terminal
//            .expectSuggestions("multiplePossible1", "multiplePossible2")
//            .expectCommandLine("cmd multiplePossible");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesPartiallyTyped() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd m");
//        terminal
//            .expectSuggestions("multiplePossible1", "multiplePossible2")
//            .expectCommandLine("cmd multiplePossible");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesForceDecision() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd m ");
//        terminal
//            .expectError()
//            .expectSuggestions("multiplePossible1", "multiplePossible2")
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesChooseFirst() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd multiplePossible1");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd multiplePossible1 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesChooseSecond() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd multiplePossible2");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd multiplePossible2 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesChooseSecondNoMoreArgs() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd multiplePossible2 ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesChooseSecondNoMoreArgsExtraSpaces() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd multiplePossible2              ");
//        terminal
//            .expectNoSuggestions()
//            .expectCommandLine("cmd multiplePossible2 ");
//        terminal.assertExpected();
//    }
//
//    @Test
//    public void singleStringParamMultiplePossibleValuesChooseSecondNoMoreArgsForce() {
//        setParams(new StringCommandParam("param", "multiplePossible1", "multiplePossible2"));
//        shell.autoComplete("cmd multiplePossible2 a");
//        terminal
//            .expectError()
//            .expectNoSuggestions()
//            .expectCommandLineNotChanged();
//        terminal.assertExpected();
//    }
//
//    private void setParams(CommandParam... params) {
//        manager.addEntry(
//            new ShellCommandImpl("cmd", "cmd", params, new CommandExecutor() {
//                @Override
//                protected ExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
//                    return success("Executed");
//                }
//            })
//        );
//    }
//}

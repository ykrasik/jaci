package com.rawcod.jerminal.command;

import com.rawcod.jerminal.TestTerminal;
import com.rawcod.jerminal.shell.Shell;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.returnvalue.ShellExecuteReturnValue;
import com.rawcod.jerminal.shell.entry.command.ShellCommand;
import com.rawcod.jerminal.shell.entry.command.ShellCommandArgs;
import com.rawcod.jerminal.shell.entry.command.ShellCommandExecutor;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.entry.parameters.string.StringShellParam;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * User: ykrasik
 * Date: 10/01/14
 */
public class CommandSingleStringParamAutoCompleteTest {
    private TestTerminal terminal;
    private Shell shell;

    private ShellManager manager;

    @Before
    public void setup() {
        manager = new ShellManager();

        terminal = new TestTerminal();
        shell = new Shell(manager, terminal, 3);

        terminal.expectSuccess();
    }

    @Test
    public void noParams() {
        setParams();
        shell.autoComplete("cmd ");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void noParamsForce() {
        setParams();
        shell.autoComplete("cmd s");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamNoPossibleValuesBlank() {
        setParams(new StringShellParam("param"));
        shell.autoComplete("cmd ");
        terminal
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamNoPossibleValuesSomeValue() {
        setParams(new StringShellParam("param"));
        shell.autoComplete("cmd someValue");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd someValue ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamNoPossibleValuesSomeValueForce() {
        setParams(new StringShellParam("param"));
        shell.autoComplete("cmd someValue ");
        terminal
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamSinglePossibleValuesBlank() {
        setParams(new StringShellParam("param", "singlePossible"));
        shell.autoComplete("cmd ");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd singlePossible ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamSinglePossibleValuesPartiallyTyped() {
        setParams(new StringShellParam("param", "singlePossible"));
        shell.autoComplete("cmd s");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd singlePossible ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamSinglePossibleValuesPartiallyTypedForceDecision() {
        setParams(new StringShellParam("param", "singlePossible"));
        shell.autoComplete("cmd s ");
        terminal
            .expectError()
            .expectSuggestions("singlePossible")
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamSinglePossibleValuesFullyTyped() {
        setParams(new StringShellParam("param", "singlePossible"));
        shell.autoComplete("cmd singlePossible");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd singlePossible ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamSinglePossibleValuesNoMoreArgs() {
        // Spaces after last param are ignored
        setParams(new StringShellParam("param", "singlePossible"));
        shell.autoComplete("cmd singlePossible             ");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd singlePossible ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamSinglePossibleValuesNoMoreArgsTyped() {
        setParams(new StringShellParam("param", "singlePossible"));
        shell.autoComplete("cmd singlePossible s");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesBlank() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd ");
        terminal
            .expectSuggestions("multiplePossible1", "multiplePossible2")
            .expectCommandLine("cmd multiplePossible");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesPartiallyTyped() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd m");
        terminal
            .expectSuggestions("multiplePossible1", "multiplePossible2")
            .expectCommandLine("cmd multiplePossible");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesForceDecision() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd m ");
        terminal
            .expectError()
            .expectSuggestions("multiplePossible1", "multiplePossible2")
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesChooseFirst() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd multiplePossible1");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd multiplePossible1 ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesChooseSecond() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd multiplePossible2");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd multiplePossible2 ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesChooseSecondNoMoreArgs() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd multiplePossible2 ");
        terminal
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesChooseSecondNoMoreArgsExtraSpaces() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd multiplePossible2              ");
        terminal
            .expectNoSuggestions()
            .expectCommandLine("cmd multiplePossible2 ");
        terminal.assertExpected();
    }

    @Test
    public void singleStringParamMultiplePossibleValuesChooseSecondNoMoreArgsForce() {
        setParams(new StringShellParam("param", "multiplePossible1", "multiplePossible2"));
        shell.autoComplete("cmd multiplePossible2 a");
        terminal
            .expectError()
            .expectNoSuggestions()
            .expectCommandLineNotChanged();
        terminal.assertExpected();
    }

    private void setParams(ShellParam... params) {
        manager.addEntry(
            new ShellCommand("cmd", "cmd", params, new ShellCommandExecutor() {
                @Override
                protected ShellExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags) {
                    return success("Executed");
                }
            })
        );
    }
}

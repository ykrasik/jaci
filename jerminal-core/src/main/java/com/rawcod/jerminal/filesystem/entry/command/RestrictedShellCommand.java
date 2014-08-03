package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.RestrictedCommandParam;
import com.rawcod.jerminal.command.parameters.manager.CommandParamManager;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 01:31
 */
public class RestrictedShellCommand implements ShellCommand {
    private final ShellCommand delegate;
    private final List<CommandParam> restrictedParams;

    public RestrictedShellCommand(ShellCommand delegate) {
        this.delegate = delegate;
        this.restrictedParams = Collections.unmodifiableList(createRestrictedParams(delegate));
    }

    private List<CommandParam> createRestrictedParams(ShellCommand command) {
        final List<CommandParam> params = new ArrayList<>(command.getParams().size());
        for (CommandParam param : command.getParams()) {
            params.add(new RestrictedCommandParam(param));
        }
        return params;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public ShellDirectory getDirectory() {
        return delegate.getDirectory();
    }

    @Override
    public ShellCommand getCommand() {
        return this;
    }

    @Override
    public List<CommandParam> getParams() {
        return restrictedParams;
    }

    @Override
    public CommandParamManager getParamManager() {
        throw restrictedException();
    }

    @Override
    public ExecuteReturnValue execute(CommandArgs args) {
        throw restrictedException();
    }

    private UnsupportedOperationException restrictedException() {
        final String message = String.format("Operation not allowed on restricted command '%s'!", getName());
        throw new UnsupportedOperationException(message);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}

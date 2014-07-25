package com.rawcod.jerminal.command;

import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;

import java.util.Map;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 14:12
 */
public class CommandArgs {
    private final Map<String, Object> args;

    public CommandArgs(Map<String, Object> args) {
        this.args = args;
    }

    public String getString(String name) {
        return getParam(name, String.class);
    }

    public int getInt(String name) {
        return getParam(name, Integer.class);
    }

    public double getDouble(String name) {
        return getParam(name, Double.class);
    }

    public boolean getBool(String name) {
        return getParam(name, Boolean.class);
    }

    public ShellDirectory getDirectory(String name) {
        return getParam(name, ShellDirectory.class);
    }

    public ShellCommand getFile(String name) {
        return getParam(name, ShellCommand.class);
    }

    private <T> T getParam(String name, Class<T> clazz) {
        final Object value = args.get(name);
        if (value == null) {
            throw new ShellException("No value defined for param '%s'!", name);
        }

        return clazz.cast(value);
    }
}

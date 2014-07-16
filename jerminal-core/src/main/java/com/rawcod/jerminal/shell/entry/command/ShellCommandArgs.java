package com.rawcod.jerminal.shell.entry.command;

import com.rawcod.jerminal.shell.entry.directory.ShellDirectory;

import java.util.Queue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class ShellCommandArgs {
    private final Queue<Object> parsedArgs;

    public ShellCommandArgs(Queue<Object> parsedArgs) {
        this.parsedArgs = parsedArgs;
    }

    public String popString() {
        return (String) parsedArgs.poll();
    }

    public int popInt() {
        return (int) parsedArgs.poll();
    }

    public boolean popBool() {
        return (boolean) parsedArgs.poll();
    }

    public ShellDirectory popDirectory() {
        return (ShellDirectory) parsedArgs.poll();
    }

    public ShellCommand popFile() {
        return (ShellCommand) parsedArgs.poll();
    }
}

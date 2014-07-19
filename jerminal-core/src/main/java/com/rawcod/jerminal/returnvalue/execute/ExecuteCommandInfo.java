package com.rawcod.jerminal.returnvalue.execute;

import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 16/07/2014
 * Time: 22:13
 */
public class ExecuteCommandInfo {
    private final List<String> path;
    private final String command;
    private final Map<String, Object> args;
    private final Set<String> flags;

    public ExecuteCommandInfo(List<String> path,
                              String command,
                              Map<String, Object> args,
                              Set<String> flags) {
        this.path = Collections.unmodifiableList(path);
        this.command = command;
        this.args = Collections.unmodifiableMap(args);
        this.flags = Collections.unmodifiableSet(flags);
    }

    public List<String> getPath() {
        return path;
    }

    public String getCommand() {
        return command;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public Set<String> getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("path", path)
            .add("command", command)
            .add("args", args)
            .add("flags", flags)
            .toString();
    }
}

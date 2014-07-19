package com.rawcod.jerminal.filesystem.entry.directory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 13/01/14
 */
public class ShellTree {
    private final String name;
    private final String description;
    private final String usage;
    private final boolean directory;
    private final List<ShellTree> children;

    public ShellTree(String name, String description, String usage, boolean directory) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.directory = directory;
        this.children = new ArrayList<>();
    }

    protected void addChild(ShellTree child) {
        children.add(child);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public boolean isDirectory() {
        return directory;
    }

    public List<ShellTree> getChildren() {
        return children;
    }
}

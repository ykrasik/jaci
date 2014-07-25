package com.rawcod.jerminal.filesystem.entry;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 15:36
 */
public abstract class AbstractShellEntry implements ShellEntry {
    private final String name;
    private final String description;

    public AbstractShellEntry(String name, String description) {
        this.name = checkNotNull(name, "name is null!");
        this.description = checkNotNull(description, "description is null!");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
            .add("name", name)
            .add("description", description)
            .toString();
    }
}

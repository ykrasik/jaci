package com.rawcod.jerminal.shell.entry.directory;

import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.ShellAutoComplete;
import com.rawcod.jerminal.shell.entry.ShellEntry;
import com.rawcod.jerminal.shell.parser.ShellStringParser;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: ykrasik
 * Date: 06/01/14
 */
public class ShellDirectory implements ShellEntry {
    private static final String THIS = ".";
    private static final String PARENT = "..";

    private static final DebugEntryComparator COMPARATOR = new DebugEntryComparator();

    private final String name;
    private final String description;
    private final ShellStringParser<ShellEntry> parser;
    private ShellDirectory parent;

    public ShellDirectory(String name) {
        this(name, null);
    }

    public ShellDirectory(String name, String description) {
        this.name = name;
        this.description = description;

        final String autoCompleteErrorFormat = String.format("Directory '%s' has no child entries with prefix:", name) + " '%s'";
        final String parseErrorFormat = String.format("Directory '%s' doesn't contain child entry:", name) + " '%s'";
        this.parser = new ShellStringParser<>(autoCompleteErrorFormat, parseErrorFormat);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public ShellAutoCompleteReturnValue autoCompleteEntry(String arg, TrieFilter<ShellEntry> filter) {
        if (PARENT.equals(arg) || THIS.equals(arg)) {
            return ShellAutoCompleteReturnValue.success(ShellAutoComplete.single(arg));
        }
        if(parser.isEmpty()) {
            final String errorMessage = String.format("Directory '%s' is empty.", name);
            return ShellAutoCompleteReturnValue.failureInvalidArgument(errorMessage);
        }
        return parser.autoComplete(arg, filter);
    }

    public ShellParseReturnValue<ShellEntry> parseEntry(String arg, TrieFilter<ShellEntry> filter) {
        if (PARENT.equals(arg)) {
            return ShellParseReturnValue.<ShellEntry>success(parent);
        }
        if (THIS.equals(arg)) {
            return ShellParseReturnValue.<ShellEntry>success(this);
        }
        if (parser.isEmpty()) {
            final String errorMessage = String.format("Directory '%s' is empty.", name);
            return ShellParseReturnValue.failureInvalidArgument(errorMessage);
        }
        return parser.parse(arg, filter);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public void install(ShellManager manager) {
        for (ShellEntry child : parser.getAllValues()) {
            child.install(manager);
        }

    }

    public ShellDirectory addEntries(ShellEntry... entries) {
        for (ShellEntry entry : entries) {
            addEntry(entry);
        }
        return this;
    }

    public ShellDirectory addEntry(ShellEntry entry) {
        if (entry == this) {
            throw new RuntimeException("Trying to add a directory to itself!");
        }
        if (entry.isDirectory()) {
            ((ShellDirectory) entry).setParent(this);
        }
        parser.addWord(entry.getName(), entry);
        return this;
    }

    private void setParent(ShellDirectory parent) {
        if (this.parent != null) {
            final String errorMessage = String.format("Directory '%s' already has a parent: '%s'", name, this.parent);
            throw new RuntimeException(errorMessage);
        }
        this.parent = parent;
    }

    public List<String> getPath() {
        final List<String> path = new ArrayList<>(6);
        addThisToPath(path);
        return path;
    }

    // Recurse so that the top-most directories add themselves first.
    private void addThisToPath(List<String> path) {
        if (this.parent != null) {
            parent.addThisToPath(path);
        }
        path.add(name);
    }

    public ShellTree listContent(boolean recursive) {
        return createShellTreeNode(this, true, !recursive);
    }

    private ShellTree createShellTreeNode(ShellEntry shellEntry, boolean recursive, boolean recurseOnce) {
        final ShellTree root = new ShellTree(shellEntry.getName(), shellEntry.getDescription(), shellEntry.getDescription(), shellEntry.isDirectory());
        if (recursive && shellEntry.isDirectory()) {
            final List<ShellEntry> children = ((ShellDirectory) shellEntry).getChildren();
            for (ShellEntry child : children) {
                final ShellTree node = createShellTreeNode(child, !recurseOnce, recurseOnce);
                root.addChild(node);
            }
        }
        return root;
    }

    private List<ShellEntry> getChildren() {
        final List<ShellEntry> shellEntries = parser.getAllValues();
        Collections.sort(shellEntries, COMPARATOR);
        return shellEntries;
    }

    @Override
    public String toString() {
        return name;
    }

    private static class DebugEntryComparator implements Comparator<ShellEntry> {
        @Override
        public int compare(ShellEntry o1, ShellEntry o2) {
            if (o1.isDirectory() && !o2.isDirectory()) {
                return 1;
            }
            if (o2.isDirectory() && !o1.isDirectory()) {
                return -1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    }
}

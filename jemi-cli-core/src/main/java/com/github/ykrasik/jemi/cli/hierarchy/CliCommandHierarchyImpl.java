/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.hierarchy;

import com.github.ykrasik.jemi.cli.CliConstants;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.directory.CommandDirectoryDef;
import com.github.ykrasik.jemi.hierarchy.CommandHierarchy;
import com.github.ykrasik.jemi.path.ParsedPath;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Delegate;

/**
 * An immutable implementation of a {@link CliCommandHierarchy}.<br>
 * Supports 2 types of commands - local commands which must belong to some {@link CliDirectory}
 * and system commands, which don't belong to any {@link CliDirectory} and are accessible from anywhere, no matter what
 * the current working directory is.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class CliCommandHierarchyImpl implements CliCommandHierarchy {
    private final CliDirectory root;

    /**
     * Contains system commands that are not associated with any specific directory (stuff like 'cd', 'ls' etc).
     * These commands only come into play in certain situations (the commandLine must not start with a '/'), but other
     * then that they are identical to regular commands. For this purpose, it's convenient to store them in a 'virtual' directory.
     */
    private final CliDirectory systemCommands;

    // TODO: Don't use lombok generated methods.
    @Getter @Setter
    @NonNull private CliDirectory workingDirectory;

    /**
     * Package visible for testing
     */
    CliCommandHierarchyImpl(@NonNull CliDirectory root, @NonNull CliDirectory systemCommands) {
        this.root = root;
        this.systemCommands = systemCommands;
        this.workingDirectory = root;
    }

    @Override
    public CliDirectory parsePathToDirectory(String rawPath) throws ParseException {
        // Parse all elements as directories.
        final ParsedPath path = parsePath(rawPath);
        return parsePathToDirectory(path);
    }

    @Override
    public CliCommand parsePathToCommand(String rawPath) throws ParseException {
        final ParsedPath path = parsePath(rawPath);

        // TODO: There has to be better way for testing eligibility for being a system command.
        if (!path.containsDelimiter()) {
            // path does not contain a '/' delimiter.
            // It could either be a systemCommands command, or a command under the current workingDirectory.
            // The last path element is the only path element is this path in this case.
            return getSystemOrWorkingDirectoryCommand(path.getLastElement());
        }

        // Path contains a '/' delimiter.
        // Parse the path until the last element as a path to a directory, and have the last directory parse the last element as a command.
        // So in "path/to/command", parse "path/to" as path to directory "to", and let "to" parse "command".
        final CliDirectory lastDirectory = parsePathToLastDirectory(path);

        // FIXME: This isn't going to work for paths that end with a delimiter. Solve this somehow in ParsedPath.
        final String commandName = path.getLastElement();
        final Opt<CliCommand> command = lastDirectory.getCommand(commandName);
        if (!command.isPresent()) {
            throw new ParseException(ParseError.INVALID_COMMAND, "Directory '%s' doesn't contain command: '%s'", lastDirectory.getName(), commandName);
        }
        return command.get();
    }

    private CliCommand getSystemOrWorkingDirectoryCommand(String name) throws ParseException {
        // If 'name' is a system command, return it.
        final Opt<CliCommand> systemCommand = systemCommands.getCommand(name);
        if (systemCommand.isPresent()) {
            return systemCommand.get();
        }

        // 'name' is not a system command, check if it is a child of the current workingDirectory.
        final Opt<CliCommand> command = workingDirectory.getCommand(name);
        if (command.isPresent()) {
            command.get();
        }

        throw new ParseException(ParseError.INVALID_COMMAND, "'%s' is not a recognized command!", name);
    }

    @Override
    public AutoComplete autoCompletePathToDirectory(String rawPath) throws ParseException {
        final ParsedPath path = parsePath(rawPath);

        // Parse the path until the last element as a path to a directory,
        // and have the last directory auto complete the last element as a directory.
        final CliDirectory lastDirectory = parsePathToLastDirectory(path);

        final String directoryNamePrefix = path.getLastElement();
        return lastDirectory.autoCompleteDirectory(directoryNamePrefix);
    }

    @Override
    public AutoComplete autoCompletePath(String rawPath) throws ParseException {
        final ParsedPath path = parsePath(rawPath);
        final String prefix = path.getLastElement();

        // TODO: There has to be better way for testing eligibility for being a system command.
        if (!path.containsDelimiter()) {
            // Path does not contain a '/' delimiter.
            // It could be either a system command or an entry from the current workingDirectory.
            final AutoComplete systemCommandsAutoComplete = systemCommands.autoCompleteCommand(prefix);
            final AutoComplete entriesAutoComplete = workingDirectory.autoCompleteEntry(prefix);
            return systemCommandsAutoComplete.union(entriesAutoComplete);
        }

        // Parse the path until the last element as a path to a directory,
        // and have the last directory auto complete the last element as a directory or command.
        final CliDirectory lastDirectory = parsePathToLastDirectory(path);
        return lastDirectory.autoCompleteEntry(prefix);
    }

    private ParsedPath parsePath(String path) throws ParseException {
        try {
            return ParsedPath.from(path);
        } catch (IllegalArgumentException e) {
            throw new ParseException(ParseError.INVALID_DIRECTORY, e.getMessage());
        }
    }

    private CliDirectory parsePathToLastDirectory(ParsedPath path) throws ParseException {
        final ParsedPath pathToLastDirectory = path.withoutLastElement();
        return parsePathToDirectory(pathToLastDirectory);
    }

    private CliDirectory parsePathToDirectory(ParsedPath path) throws ParseException {
        // If the path starts with '/', it starts from root.
        CliDirectory currentDirectory = path.startsWithDelimiter() ? root : workingDirectory;
        for (String directoryName : path) {
            currentDirectory = parseChildDirectory(currentDirectory, directoryName);
        }
        return currentDirectory;
    }

    private CliDirectory parseChildDirectory(CliDirectory currentDirectory, String name) throws ParseException {
        if (CliConstants.PATH_THIS.equals(name)) {
            return currentDirectory;
        }

        if (CliConstants.PATH_PARENT.equals(name)) {
            final Opt<CliDirectory> parent = currentDirectory.getParent();
            if (!parent.isPresent()) {
                throw new ParseException(ParseError.INVALID_DIRECTORY, "Directory '%s' doesn't have a parent.", currentDirectory.getName());
            }
            return parent.get();
        }

        final Opt<CliDirectory> childDirectory = currentDirectory.getDirectory(name);
        if (!childDirectory.isPresent()) {
            throw new ParseException(ParseError.INVALID_DIRECTORY, "Directory '%s' doesn't contain directory: '%s'", currentDirectory.getName(), name);
        }
        return childDirectory.get();
    }

    // TODO: Javadoc
    public static CliCommandHierarchyImpl from(CommandHierarchy hierarchy) {
        // Create hierarchy with the parameter as the root.
        final CommandDirectoryDef rootDef = hierarchy.getRoot();
        final CliDirectory root = CliDirectory.fromDef(rootDef);

        // Create system commands 'virtual' directory.
        // System commands need to operate on an already built hierarchy, but... we are exactly in the process of building one.
        // In order to fully build a hierarchy, we must provide a set of system commands.
        // This is a cyclic dependency - resolved through the use of a 'promise' object, which will delegate all calls to the
        // concrete hierarchy, once it's built.
        final CliCommandHierarchyPromise hierarchyPromise = new CliCommandHierarchyPromise();
        final CliDirectory systemCommands = CliSystemCommandFactory.from(hierarchyPromise);

        // Update the 'promise' hierarchy with the concrete implementation.
        final CliCommandHierarchyImpl cliHierarchy = new CliCommandHierarchyImpl(root, systemCommands);
        hierarchyPromise.setHierarchy(cliHierarchy);
        return cliHierarchy;
    }

    /**
     * A {@link CliCommandHierarchy} that promises to <b>eventually</b> contain a concrete implementation of a {@link CliCommandHierarchy}.
     * Required in order to resolve dependency issues between system commands and the immutability of {@link CliCommandHierarchyImpl}.
     * System commands require a concrete {@link CliCommandHierarchy} to operate on, but {@link CliCommandHierarchyImpl} requires
     * all system commands to be available at construction time - cyclic dependency.
     * So this class was born as a compromise.
     */
    private static class CliCommandHierarchyPromise implements CliCommandHierarchy {
        @Delegate
        @Setter
        private CliCommandHierarchy hierarchy;
    }
}

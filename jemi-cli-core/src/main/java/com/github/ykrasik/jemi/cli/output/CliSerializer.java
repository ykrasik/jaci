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

package com.github.ykrasik.jemi.cli.output;

import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.core.IdentifiableComparators;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor
public class CliSerializer {
    @NonNull private final CliOutput output;

    // TODO: JavaDoc
    public void printDirectory(CliDirectory directory, boolean recursive) {
        printDirectory(directory, recursive, 0);
    }

    private void printDirectory(CliDirectory directory, boolean recursive, int depth) {
        final StringBuilder sb = appendTabs(depth);

        // Print root directory name.
        sb.append('[');
        sb.append(directory.getName());
        sb.append(']');
        output.println(sb.toString());

        // Print child commands.
        final List<CliCommand> commands = new ArrayList<>(directory.getChildCommands());
        Collections.sort(commands, IdentifiableComparators.nameComparator());
        for (CliCommand command : commands) {
            printCommand(command, depth + 1, Opt.<CliBoundParams>absent());
        }

        if (recursive) {
            // Print child directories.
            final List<CliDirectory> directories = new ArrayList<>(directory.getChildDirectories());
            Collections.sort(directories, IdentifiableComparators.nameComparator());
            for (CliDirectory childDirectory : directories) {
                printDirectory(childDirectory, true, depth + 1);
            }
        }
    }

    // TODO: JavaDoc
    public void printCommand(CliCommand command) {
        printCommand(command, true, 0);
    }

    private void printCommand(CliCommand command, boolean withParams, int depth) {
        asd
    }

    private void printCommand(CliCommand command, int depth, Opt<CliBoundParams> boundParams) {
        final StringBuilder sb = appendTabs(depth);

        // Print name : description
        sb.append(command.getName());
        sb.append(" : ");
        sb.append(command.getDescription());
        output.println(sb.toString());

        // Print params.
        if (boundParams.isPresent()) {
            printBoundParams(command, boundParams.get(), depth + 1);
        }
    }

    private void printBoundParams(CliCommand command, CliBoundParams boundParams, int depth) {
        final Opt<CliParam> currentParam = boundParams.getCurrentParam();
        for (CliParam param : command.getParams()) {
            final Opt<String> value = boundParams.getBoundValue(param);
            final boolean isCurrent = currentParam.isPresent() && currentParam.get() == param;
            printParam(param, depth + 1, value, false, isCurrent);
        }
    }

    // TODO: This is ugly, find a better way.
    private void printParam(CliParam param,
                            int depth,
                            Opt<String> value,
                            boolean withDescription,
                            boolean isCurrent) {
        final StringBuilder sb = appendTabs(depth);

        // Surround the current param being parsed with -> <-
        if (isCurrent) {
            sb.append("-> ");
            sb.append(getTab());
        } else {
            sb.append(getTab());
        }

        sb.append(param.toExternalForm());
        if (value.isPresent()) {
            sb.append(" = ");
            sb.append(value.get());
        }

        if (withDescription) {
            sb.append(" : ");
            sb.append(param.getIdentifier().getDescription());
        }

        // Actually, value.isPresent and isCurrent cannot both be true at the same time.
        if (isCurrent) {
            sb.append(getTab());
            sb.append(" <-");
        }

        output.println(sb.toString());
    }

    // TODO: JavaDoc
    public void printException(Exception e) {
        output.errorPrintln(e.toString());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            output.errorPrintln(getTab() + stackTraceElement.toString());
        }
    }

    private StringBuilder appendTabs(int tabs) {
        final String tab = getTab();
        final StringBuilder sb = new StringBuilder(tab.length() * tabs);
        for (int i = 0; i < tabs; i++) {
            sb.append(tab);
        }
        return sb;
    }

    private String getTab() {
        return output.getConfig().getTab();
    }
}

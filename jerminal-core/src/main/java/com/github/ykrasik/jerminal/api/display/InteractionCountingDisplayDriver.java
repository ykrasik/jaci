/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.display;

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;

import java.util.List;
import java.util.Objects;

/**
 * A decorator for a {@link DisplayDriver} that counts the amount of interactions it had.
 *
 * @author Yevgeny Krasik
 */
public class InteractionCountingDisplayDriver implements DisplayDriver {
    private final DisplayDriver displayDriver;
    private int interactions;

    public InteractionCountingDisplayDriver(DisplayDriver displayDriver) {
        this.displayDriver = Objects.requireNonNull(displayDriver);
    }

    public int getInteractions() {
        return interactions;
    }

    @Override
    public void begin() {
        interactions = 0;
        displayDriver.begin();
    }

    @Override
    public void end() {
        displayDriver.end();
    }

    @Override
    public void displayWelcomeMessage(String welcomeMessage) {
        interactions++;
        displayDriver.displayWelcomeMessage(welcomeMessage);
    }

    @Override
    public void displayEmptyLine() {
        interactions++;
        displayDriver.displayEmptyLine();
    }

    @Override
    public void displayText(String text) {
        interactions++;
        displayDriver.displayText(text);
    }

    @Override
    public void displayCommandInfo(CommandInfo commandInfo) {
        interactions++;
        displayDriver.displayCommandInfo(commandInfo);
    }

    @Override
    public void displaySuggestions(Suggestions suggestions) {
        interactions++;
        displayDriver.displaySuggestions(suggestions);
    }

    @Override
    public void displayDirectory(ShellDirectory directory) {
        interactions++;
        displayDriver.displayDirectory(directory);
    }

    @Override
    public void displayCommand(Command command) {
        interactions++;
        displayDriver.displayCommand(command);
    }

    @Override
    public void setWorkingDirectory(List<String> path) {
        interactions++;
        displayDriver.setWorkingDirectory(path);
    }

    @Override
    public void displayParseError(ParseError error, String errorMessage) {
        interactions++;
        displayDriver.displayParseError(error, errorMessage);
    }

    @Override
    public void displayException(Exception e) {
        interactions++;
        displayDriver.displayException(e);
    }
}

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

package com.github.ykrasik.jaci.cli.javafx;

import com.github.ykrasik.jaci.cli.Cli;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

/**
 * Links {@link KeyEvent}s to {@link Cli} calls.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxCliEventHandler implements EventHandler<KeyEvent> {
    private final Cli cli;

    public JavaFxCliEventHandler(Cli cli) {
        this.cli = Objects.requireNonNull(cli, "cli");
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
                cli.execute();
                keyEvent.consume();
                break;

            case TAB:
                cli.assist();
                keyEvent.consume();
                break;

            case UP:
                cli.setPrevCommandLineFromHistory();
                keyEvent.consume();
                break;

            case DOWN:
                cli.setNextCommandLineFromHistory();
                keyEvent.consume();
                break;

            default:
                break;
        }
    }
}

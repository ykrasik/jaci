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

package com.github.ykrasik.jerminal.libgdx.impl;

import com.badlogic.gdx.graphics.Color;
import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.api.display.terminal.Terminal;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalDisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalGuiController;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalSerializer;

/**
 * A specialized version of a {@link TerminalDisplayDriver} for LibGdx.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxTerminalDisplayDriver extends TerminalDisplayDriver {
    public LibGdxTerminalDisplayDriver(Terminal terminal, TerminalGuiController guiController, TerminalSerializer serializer) {
        super(terminal, guiController, serializer);
    }

    @Override
    public void displaySuggestions(Suggestions suggestions) {
        final String suggestionsStr = getSerializer().serializeSuggestions(suggestions);
        ((LibGdxTerminal) getTerminal()).println(suggestionsStr, Color.YELLOW);
    }
}

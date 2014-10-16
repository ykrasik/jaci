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

package com.github.ykrasik.jerminal.libgdx.terminal;

import com.github.ykrasik.jerminal.api.display.terminal.DefaultTerminalSerializer;

/**
 * A specialized version of a {@link com.github.ykrasik.jerminal.api.display.terminal.TerminalSerializer} for LibGdx.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxTerminalSerializer extends DefaultTerminalSerializer {
    @Override
    protected void appendDepthSpaces(StringBuilder sb, int depth) {
        // LibGdx seems to ignore leading spaces, so we must prefix them with something.
        sb.append('|');
        super.appendDepthSpaces(sb, depth);
    }

    @Override
    protected String getTab() {
        return "    ";
    }
}

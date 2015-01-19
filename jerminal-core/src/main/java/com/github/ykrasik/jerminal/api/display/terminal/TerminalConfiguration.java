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

package com.github.ykrasik.jerminal.api.display.terminal;

import java.util.Objects;

/**
 * Contains various parameters regarding a {@link Terminal}'s configuration.<br>
 *
 * @author Yevgeny Krasik
 */
public class TerminalConfiguration {
    public static final TerminalConfiguration DEFAULT_WHITE = new Builder(TerminalColor.WHITE).build();
    public static final TerminalConfiguration DEFAULT_BLACK = new Builder(TerminalColor.BLACK).build();

    private final TerminalColor textColor;
    private final TerminalColor errorColor;
    private final TerminalColor suggestionsColor;
    private final TerminalColor directoryColor;
    private final TerminalColor commandColor;
    private final TerminalColor paramColor;

    private TerminalConfiguration(Builder builder) {
        this.textColor = builder.textColor;
        this.errorColor = builder.errorColor;
        this.suggestionsColor = builder.suggestionsColor;
        this.directoryColor = builder.directoryColor;
        this.commandColor = builder.commandColor;
        this.paramColor = builder.paramColor;
    }

    /**
     * @return Color that should be used to print regular text.
     *         Cannot be null.
     */
    public TerminalColor getTextColor() {
        return textColor;
    }

    /**
     * @return Color that should be used to print error text.
     *         If null, will use the color returned by {@link #getTextColor()}.
     */
    public TerminalColor getErrorColor() {
        return errorColor;
    }

    /**
     * @return Color that should be used to print suggestions.
     *         If null, will use the color returned by {@link #getTextColor()}.
     */
    public TerminalColor getSuggestionsColor() {
        return suggestionsColor;
    }

    /**
     * @return Color that should be used to print directories.
     *         If null, will use the color returned by {@link #getTextColor()}.
     */
    public TerminalColor getDirectoryColor() {
        return directoryColor;
    }

    /**
     * @return Color that should be used to print commands.
     *         If null, will use the color returned by {@link #getTextColor()}.
     */
    public TerminalColor getCommandColor() {
        return commandColor;
    }

    /**
     * @return Color that should be used to print command parameters.
     *         If null, will use the color returned by {@link #getTextColor()}.
     */
    public TerminalColor getParamColor() {
        return paramColor;
    }

    /**
     * Builder for a {@link TerminalConfiguration}.
     */
    public static class Builder {
        private final TerminalColor textColor;
        private TerminalColor errorColor = TerminalColor.RED;
        private TerminalColor suggestionsColor = TerminalColor.YELLOW;
        private TerminalColor directoryColor = TerminalColor.ORANGE;
        private TerminalColor commandColor = TerminalColor.GREEN;
        private TerminalColor paramColor = TerminalColor.BLUE;

        public Builder(TerminalColor textColor) {
            this.textColor = Objects.requireNonNull(textColor);
        }

        public TerminalConfiguration build() {
            return new TerminalConfiguration(this);
        }

        public Builder setErrorColor(TerminalColor errorColor) {
            this.errorColor = errorColor;
            return this;
        }

        public Builder setSuggestionsColor(TerminalColor suggestionsColor) {
            this.suggestionsColor = suggestionsColor;
            return this;
        }

        public Builder setDirectoryColor(TerminalColor directoryColor) {
            this.directoryColor = directoryColor;
            return this;
        }

        public Builder setCommandColor(TerminalColor commandColor) {
            this.commandColor = commandColor;
            return this;
        }

        public Builder setParamColor(TerminalColor paramColor) {
            this.paramColor = paramColor;
            return this;
        }
    }
}

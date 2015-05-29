/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.cli.assist;

import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The return value of an assist operation.<br>
 * Contains info about the command and auto complete suggestions.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor
public class AssistInfo {
    @NonNull private final Opt<CommandInfo> commandInfo;
    @NonNull private final AutoComplete autoComplete;

    /**
     * @return The command info.
     */
    public Opt<CommandInfo> getCommandInfo() {
        return commandInfo;
    }

    /**
     * @return The auto-complete suggestions.
     */
    public AutoComplete getAutoComplete() {
        return autoComplete;
    }

    public static AssistInfo noCommandInfo(AutoComplete autoComplete) {
        return new AssistInfo(Opt.<CommandInfo>absent(), autoComplete);
    }
}

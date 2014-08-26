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

package com.github.ykrasik.jerminal.internal.returnvalue;

/**
 * For an auto complete operation, this is the type of the value being auto completed.
 *
 * @author Yevgeny Krasik
 */
public enum AutoCompleteType {
    // TODO: Get these constants out of somewhere more specific.
    // FIXME: Doesn't belong here.
    DIRECTORY('/'),
    COMMAND(' '),
    COMMAND_PARAM_NAME('='),
    COMMAND_PARAM_VALUE(' '),
    COMMAND_PARAM_FLAG(' ');

    private final char suffix;

    AutoCompleteType(char suffix) {
        this.suffix = suffix;
    }

    public char getSuffix() {
        return suffix;
    }
}

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

package com.github.ykrasik.jerminal;

import java.util.Arrays;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
public final class ShellConstants {
    private ShellConstants() {
    }

    public static final String FILE_SYSTEM_DELIMITER = "/";
    public static final String FILE_SYSTEM_THIS = ".";
    public static final String FILE_SYSTEM_PARENT = "..";
    public static final String FILE_SYSTEM_DESCRIPTION_DELIMITER = ":";

    public static final String ARG_VALUE_DELIMITER = "=";

    private static final List<String> RESERVED_CHARS = Arrays.asList(
        FILE_SYSTEM_DELIMITER, FILE_SYSTEM_THIS, FILE_SYSTEM_PARENT, FILE_SYSTEM_DESCRIPTION_DELIMITER,
        ARG_VALUE_DELIMITER
    );

    public static boolean isValidName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        for (String reservedChar : RESERVED_CHARS) {
            if (name.contains(reservedChar)) {
                return false;
            }
        }
        return true;
    }
}

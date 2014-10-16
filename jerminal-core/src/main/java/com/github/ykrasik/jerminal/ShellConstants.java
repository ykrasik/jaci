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
 * Constants used throughout the whole program.
 *
 * @author Yevgeny Krasik
 */
public final class ShellConstants {
    private ShellConstants() {
    }

    /**
     * Path delimiter.
     */
    public static final String FILE_SYSTEM_DELIMITER = "/";

    /**
     * In a path, will be interpreted as 'this directory'. Optional.
     */
    public static final String FILE_SYSTEM_THIS = ".";

    /**
     * In a path, will be interpreted as 'parent directory'. Optional.
     */
    public static final String FILE_SYSTEM_PARENT = "..";

    /**
     * When creating the file system, descriptions can be assigned to directories.
     * This is the delimiter for the description.
     */
    public static final String FILE_SYSTEM_DESCRIPTION_DELIMITER = ":";

    /**
     * When passing args by name, this is the delimiter between the parameter name and value.
     */
    public static final String ARG_VALUE_DELIMITER = "=";

    private static final List<String> NOT_ALLOWED_IN_NAME = Arrays.asList(
        FILE_SYSTEM_DELIMITER, FILE_SYSTEM_THIS, FILE_SYSTEM_PARENT, FILE_SYSTEM_DESCRIPTION_DELIMITER,
        ARG_VALUE_DELIMITER, " ", "\t", "\n", "\r"
    );

    /**
     * @param name Name to check.
     * @return True if the name is valid. A valid name cannot contain any of the reserved characters.
     */
    public static boolean isValidName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        for (String notAllowed : NOT_ALLOWED_IN_NAME) {
            if (name.contains(notAllowed)) {
                return false;
            }
        }
        return true;
    }
}

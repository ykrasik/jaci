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

package com.github.ykrasik.jerminal.api;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public final class Constants {
    private Constants() { }

    /**
     * Path delimiter.
     */
    public static final char PATH_DELIMITER = '/';

    /**
     * Path delimiter as a {@link String}.
     */
    public static final String PATH_DELIMITER_STRING = String.valueOf(PATH_DELIMITER);
}

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

package com.github.ykrasik.jemi.cli.assist;

import lombok.Data;
import lombok.NonNull;

/**
 * Assistance information for a command's parameters.
 * Includes parameters that were already bound (and parsed) as well as auto-complete suggestions for the next parameter.
 *
 * @author Yevgeny Krasik
 */
@Data
public class ParamAssistInfo {
    /**
     * The parameters that have already been parsed, as well as the next parameter to parse, if one exists.
     */
    @NonNull private final BoundParams boundParams;

    /**
     * Auto-complete suggestions for the next parameter to parse.
     */
    @NonNull private final AutoComplete autoComplete;
}

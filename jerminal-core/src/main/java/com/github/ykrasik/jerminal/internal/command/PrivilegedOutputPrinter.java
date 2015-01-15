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

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.api.command.OutputPrinter;

/**
 * An {@link OutputPrinter} that provides additional methods.
 * For internal use, should never be used externally.
 *
 * @author Yevgeny Krasik
 */
public interface PrivilegedOutputPrinter extends OutputPrinter {
    /**
     * @return Whether any of the {@link OutputPrinter}'s API methods were invoked.
     */
    boolean hasInteractions();

    /**
     * Suppresses the default 'command executed successfully' message that appears if no other interactions are
     * detected by {@link #hasInteractions()}.
     */
    void suppressDefaultExecutionMessage();

    /**
     * @return Whether the command requested to suppress the default 'command executed successfully' message that appears
     * if no other interactions are detected by {@link #hasInteractions()}.
     */
    boolean isSuppressDefaultExecutionMessage();
}

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

package com.github.ykrasik.jemi.cli.libgdx;

import com.github.ykrasik.jemi.api.*;

/**
 * Toggle commands are special commands that take a single optional boolean parameter and toggle the state of some
 * component on or off.
 *
 * @see ToggleCommand
 *
 * @author Yevgeny Krasik
 */
@CommandPath("toggle")
public class ToggleSample {
    @ToggleCommand(description = "A toggle command")
    public ToggleCommandStateAccessor toggle() {
        return new ToggleCommandStateAccessor() {
            private boolean state;

            @Override
            public void set(boolean value) {
                state = value;
            }

            @Override
            public boolean get() {
                return state;
            }
        };
    }
}

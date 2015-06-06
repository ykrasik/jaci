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

package com.github.ykrasik.jemi.cli.libgdx;

/**
 * A listener that is invoked whenever an actor's visibility state changes.
 *
 * @author Yevgeny Krasik
 */
public interface VisibleListener {
    /**
     * Invoked when the actor to which this listener is attached changed it's visiblity state.
     * It was either visible and became invisible, or the other way.
     *
     * @param wasVisible Whether the actor was previously visible.
     * @param isVisible Whether the actor is currently visible.
     */
    void onVisibleChange(boolean wasVisible, boolean isVisible);
}

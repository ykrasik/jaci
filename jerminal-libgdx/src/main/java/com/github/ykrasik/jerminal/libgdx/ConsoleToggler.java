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

package com.github.ykrasik.jerminal.libgdx;

/**
 * @author Yevgeny Krasik
 *
 * It is highly recommended to only toggle on Ctrl+{key} or some other combination that is not just
 * a simple key, because that even will be propagated to the textField and that key will be typed into
 * the textField after the console was activated.
 */
// FIXME: JavaDoc
public interface ConsoleToggler {
    boolean shouldToggle(int keycode);
}

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

package com.github.ykrasik.jerminal.internal.filesystem;

import com.github.ykrasik.jerminal.internal.Describable;

/**
 * Represents an entry in a {@link ShellFileSystem}.<br>
 * Can be either a {@link com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory directory}
 * or a {@link com.github.ykrasik.jerminal.api.command.Command command}.
 *
 * @author Yevgeny Krasik
 */
public interface ShellEntry extends Describable {
    /**
     * Returns 'true' if this {@link ShellEntry entry} is a directory that can contain other {@link ShellEntry entries}.
     */
    boolean isDirectory();
}

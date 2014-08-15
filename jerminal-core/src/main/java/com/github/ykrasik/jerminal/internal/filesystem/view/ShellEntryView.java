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

package com.github.ykrasik.jerminal.internal.filesystem.view;

import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;

import java.util.List;

/**
 * A view of a {@link com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem fileSystem} hierarchy.
 *
 * @author Yevgeny Krasik
 */
public interface ShellEntryView extends ShellEntry {
    /**
     * Returns the children of this entry. If this entry is not a directory, returns an empty list.
     */
    List<ShellEntryView> getChildren();
}

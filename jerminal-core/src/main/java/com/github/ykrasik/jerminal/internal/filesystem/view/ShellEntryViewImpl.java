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

import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;

import java.util.Collections;
import java.util.List;

/**
 * An <b>immutable</b> implementation for a {@link com.github.ykrasik.jerminal.api.filesystem.ShellEntryView}.
 *
 * @author Yevgeny Krasik
 */
public class ShellEntryViewImpl extends AbstractDescribable implements ShellEntryView {
    private final boolean directory;
    private final List<ShellEntryView> children;

    public ShellEntryViewImpl(String name, String description, boolean directory, List<ShellEntryView> children) {
        super(name, description);
        this.directory = directory;
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public List<ShellEntryView> getChildren() {
        return children;
    }
}

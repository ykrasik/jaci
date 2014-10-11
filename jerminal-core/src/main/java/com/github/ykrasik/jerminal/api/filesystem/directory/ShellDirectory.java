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

package com.github.ykrasik.jerminal.api.filesystem.directory;

import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.internal.Describable;

import java.util.Collection;

// FIXME: Incorrect JavaDoc
/**
 * A container for {@link ShellDirectory directories} and {@link Command commands}.
 *
 * @author Yevgeny Krasik
 */
public interface ShellDirectory extends Describable {

    // FIXME: JavaDoc

    Collection<ShellDirectory> getDirectories();

    Collection<Command> getCommands();
}

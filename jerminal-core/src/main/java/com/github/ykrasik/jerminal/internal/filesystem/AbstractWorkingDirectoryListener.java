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

package com.github.ykrasik.jerminal.internal.filesystem;

import com.github.ykrasik.jerminal.ShellConstants;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public abstract class AbstractWorkingDirectoryListener implements WorkingDirectoryListener {
    @Override
    public void onWorkingDirectoryChanged(List<InternalShellDirectory> path) {
        // All paths should start with '/'.
        final StringBuilder sb = new StringBuilder();
        sb.append(ShellConstants.FILE_SYSTEM_DELIMITER);

        // The first element is always the root, skip it.
        for (int i = 1; i < path.size(); i++) {
            final InternalShellDirectory directory = path.get(i);
            sb.append(directory.getName());
            sb.append(ShellConstants.FILE_SYSTEM_DELIMITER);
        }
        setCurrentPath(sb.toString());
    }

    protected abstract void setCurrentPath(String currentPathStr);
}

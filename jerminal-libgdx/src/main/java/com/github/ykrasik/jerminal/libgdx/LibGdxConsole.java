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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

/**
 * A {@link Table} that keeps a list of listeners that are called whenever that table is made visible or invisible with
 * {@link #setVisible(boolean)}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxConsole extends Table {
    private final Array<VisibleListener> visibleListeners = new Array<>(2);

    public LibGdxConsole() {
    }

    public LibGdxConsole(Skin skin) {
        super(skin);
    }

    public void addVisibleListener(VisibleListener listener) {
        visibleListeners.add(listener);
    }

    public void removeVisibleListener(VisibleListener listener) {
        visibleListeners.removeValue(listener, true);
    }

    @Override
    public void setVisible(boolean visible) {
        final boolean wasVisible = isVisible();
        for (VisibleListener listener : visibleListeners) {
            listener.onVisibleChange(wasVisible, visible);
        }
        super.setVisible(visible);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            // Call the listeners when we're first added to a stage.
            final boolean isVisible = isVisible();
            final boolean wasVisible = !isVisible;
            for (VisibleListener listener : visibleListeners) {
                listener.onVisibleChange(wasVisible, isVisible);
            }
        }
    }
}

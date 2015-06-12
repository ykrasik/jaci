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

package com.github.ykrasik.jaci.cli.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * @author Yevgeny Krasik
 */
public class LibGdxCliExample extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Jerminal";
        config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
        config.fullscreen = false;
        config.height -= 150;
        config.width -= 100;
        config.resizable = true;
        config.x = 50;
        config.y = 50;

        new LwjglApplication(new LibGdxCliExample(), config);
    }

    @Override
    public void create() {
        final LibGdxCli cli = new LibGdxCli.Builder()
            .processClass(PathSample1.class)
            .processClass(PathSample2.class)
            .processClass(MandatoryParamsSample.class)
            .processObject(new OptionalParamsSample())  // Just to show that this is also possible.
            .processClass(StringParamSample.class)
            .processClass(ToggleSample.class)
            .build();

        stage = new Stage();
        stage.addActor(cli);
        stage.addListener(new LibGdxVisibilityToggler(cli));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height);
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }
}

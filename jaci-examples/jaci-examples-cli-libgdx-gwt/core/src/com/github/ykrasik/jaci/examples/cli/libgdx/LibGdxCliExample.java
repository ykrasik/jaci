/******************************************************************************
 * Copyright (c) 2016 Yevgeny Krasik.                                         *
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

package com.github.ykrasik.jaci.examples.cli.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.ykrasik.jaci.cli.libgdx.LibGdxCli;
import com.github.ykrasik.jaci.cli.libgdx.LibGdxCliBuilder;
import com.github.ykrasik.jaci.cli.libgdx.LibGdxVisibilityToggler;
import com.github.ykrasik.jaci.examples.*;

/**
 * Example usage of LibGdx CLI, using the LibGdx reflection API
 * (compatible with GWT, but offers limited reflection features).
 *
 * Java reflection API is supplied by the dependency 'jaci-libgdx-cli-gwt' in
 * our Gradle file 'jaci-examples-cli-libgdx-gwt.gradle'
 *
 * The command classes themselves are located in the parent 'jaci-examples' project.
 *
 * run through Gradle: gradlew jaci-examples:jaci-examples-cli-libgdx-gwt:html:superDev
 * and then navigate to http://localhost:8080/html/
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCliExample extends ApplicationAdapter {
	private Stage stage;

	@Override
	public void create() {
		final LibGdxCli cli = new LibGdxCliBuilder()
			.processClasses(BasicCommands.class, PathCommands1.class, PathCommands2.class)
			// Can also process objects instead of classes.
			.process(new MandatoryParamsCommands(), new OptionalParamsCommands(), new StringParamCommands())
			.processClasses(EnumCommands.class, InnerClassCommands.class)
			.build();

		stage = new Stage();
		stage.addActor(cli);

		// Add an InputListener that will toggle the CLI's visibility on and off on the default ` key.
		stage.addListener(new LibGdxVisibilityToggler(cli));

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.act();
		stage.draw();
	}
}

/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
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

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.ykrasik.jaci.reflection.JavaReflectionAccessor;

/**
 * A builder for a {@link LibGdxCli}.
 * Builds a CLI with a default skin, unless a custom skin is specified via {@link #setSkin(Skin)}.<br>
 * The main methods to use are {@link #processClasses(Class[])} and {@link #process(Object[])} which process
 * a class and add any annotated methods as commands to this builder.
 *
 * Uses Java reflection API, which supports full reflection capabilities (including parameter annotations),
 * but will not work with GWT.
 */
public class LibGdxCliBuilder extends LibGdxCli.AbstractBuilder {
    static {
        // Set reflection to the Java API.
        JavaReflectionAccessor.install();
    }
}

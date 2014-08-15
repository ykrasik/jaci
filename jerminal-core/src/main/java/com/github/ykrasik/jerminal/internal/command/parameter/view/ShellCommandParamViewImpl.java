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

package com.github.ykrasik.jerminal.internal.command.parameter.view;

import com.github.ykrasik.jerminal.api.command.parameter.view.ShellCommandParamView;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamType;

/**
 * An implementation for a {@link ShellCommandParamView}.
 *
 * @author Yevgeny Krasik
 */
public class ShellCommandParamViewImpl extends AbstractDescribable implements ShellCommandParamView {
    private final ParamType type;
    private final String externalForm;

    public ShellCommandParamViewImpl(String name, String description, ParamType type, String externalForm) {
        super(name, description);
        this.type = type;
        this.externalForm = externalForm;
    }

    @Override
    public ParamType getType() {
        return type;
    }

    @Override
    public String getExternalForm() {
        return externalForm;
    }

    @Override
    public String toString() {
        return externalForm;
    }
}

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

package com.github.ykrasik.jaci.cli.assist;

import java.util.Objects;

/**
 * Assistance information for a command's parameters.
 * Includes parameters that were already bound (and parsed) as well as auto-complete suggestions for the next parameter.
 *
 * @author Yevgeny Krasik
 */
public class ParamAssistInfo {
    private final BoundParams boundParams;
    private final AutoComplete autoComplete;

    public ParamAssistInfo(BoundParams boundParams, AutoComplete autoComplete) {
        this.boundParams = Objects.requireNonNull(boundParams, "boundParams");
        this.autoComplete = Objects.requireNonNull(autoComplete, "autoComplete");
    }

    /**
     * @return Parameters that have already been parsed, as well as the next parameter to parse, if one exists.
     */
    public BoundParams getBoundParams() {
        return boundParams;
    }

    /**
     * @return Auto-complete suggestions for the next parameter to parse.
     */
    public AutoComplete getAutoComplete() {
        return autoComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ParamAssistInfo that = (ParamAssistInfo) o;

        if (!boundParams.equals(that.boundParams)) {
            return false;
        }
        return autoComplete.equals(that.autoComplete);

    }

    @Override
    public int hashCode() {
        int result = boundParams.hashCode();
        result = 31 * result + autoComplete.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParamAssistInfo{");
        sb.append("boundParams=").append(boundParams);
        sb.append(", autoComplete=").append(autoComplete);
        sb.append('}');
        return sb.toString();
    }
}

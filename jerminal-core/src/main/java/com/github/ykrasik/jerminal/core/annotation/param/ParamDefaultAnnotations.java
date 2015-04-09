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

package com.github.ykrasik.jerminal.core.annotation.param;

import com.github.ykrasik.jerminal.api.BoolParam;
import com.github.ykrasik.jerminal.api.DoubleParam;
import com.github.ykrasik.jerminal.api.IntParam;
import com.github.ykrasik.jerminal.api.StringParam;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@Accessors(fluent = true)
@UtilityClass
public final class ParamDefaultAnnotations {
    private static final String[] EMPTY = { };

    @Getter private static final StringParam defaultStringParam = new StringParam() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return StringParam.class;
        }

        @Override
        public String value() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String[] accepts() {
            return EMPTY;
        }

        @Override
        public String supplier() {
            return "";
        }

        @Override
        public boolean optional() {
            return false;
        }

        @Override
        public String defaultValue() {
            return "";
        }
    };

    @Getter private static final BoolParam defaultBoolParam = new BoolParam() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return BoolParam.class;
        }

        @Override
        public String value() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public boolean optional() {
            return false;
        }

        @Override
        public boolean defaultValue() {
            return false;
        }
    };

    @Getter private static final IntParam defaultIntParam = new IntParam() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return IntParam.class;
        }

        @Override
        public String value() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public boolean optional() {
            return false;
        }

        @Override
        public int defaultValue() {
            return 0;
        }
    };

    @Getter private static final DoubleParam defaultDoubleParam = new DoubleParam() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return DoubleParam.class;
        }

        @Override
        public String value() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public boolean optional() {
            return false;
        }

        @Override
        public double defaultValue() {
            return 0.0;
        }
    };
}

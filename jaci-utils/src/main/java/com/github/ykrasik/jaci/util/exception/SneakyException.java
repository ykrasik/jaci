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

package com.github.ykrasik.jaci.util.exception;

/**
 * Taken from http://proofbyexample.com/sneakythrow-avoid-checked-exceptions.html
 */
public class SneakyException {
    /**
     * Throws {@code t}, even if the declared throws clause doesn't permit it.
     * This is a terrible – but terribly convenient – hack that makes it easy to
     * catch and rethrow exceptions after cleanup. See Java Puzzlers #43.
     */
    public static RuntimeException sneakyThrow(Throwable t) {
        return SneakyException.<Error>doSneakyThrow(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException doSneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}

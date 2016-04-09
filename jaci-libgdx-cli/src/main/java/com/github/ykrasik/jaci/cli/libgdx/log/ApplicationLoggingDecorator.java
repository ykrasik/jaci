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

package com.github.ykrasik.jaci.cli.libgdx.log;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Clipboard;
import com.github.ykrasik.jaci.cli.libgdx.LibGdxCli;

import java.util.Objects;

/**
 * A decorator for a LibGdx {@link Application} that sends all logging calls
 * ({@link #error(String, String)}, {@link #error(String, String, Throwable)},
 * {@link #log(String, String)}, {@link #log(String, String, Throwable)},
 * {@link #debug(String, String)}, {@link #debug(String, String, Throwable)})
 * to a provided {@link LibGdxCli}, as well as to the decorated {@link Application}.
 *
 * @author Yevgeny Krasik
 */
public class ApplicationLoggingDecorator implements Application {
    private final Application delegate;
    private final LibGdxCli cli;

    protected int logLevel;

    public ApplicationLoggingDecorator(Application delegate, LibGdxCli cli) {
        this.delegate = Objects.requireNonNull(delegate);
        this.cli = Objects.requireNonNull(cli);

        logLevel = delegate.getLogLevel();
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return delegate.getApplicationListener();
    }

    @Override
    public Graphics getGraphics() {
        return delegate.getGraphics();
    }

    @Override
    public Audio getAudio() {
        return delegate.getAudio();
    }

    @Override
    public Input getInput() {
        return delegate.getInput();
    }

    @Override
    public Files getFiles() {
        return delegate.getFiles();
    }

    @Override
    public Net getNet() {
        return delegate.getNet();
    }

    @Override
    public void debug(String tag, String message) {
        if (logLevel >= LOG_DEBUG) {
            println(tag + ": " + message);
        }
        delegate.debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) {
            println(tag + ": " + message);
            printException(exception);
        }
        delegate.debug(tag, message, exception);
    }

    @Override
    public void log(String tag, String message) {
        if (logLevel >= LOG_INFO) {
            println(tag + ": " + message);
        }
        delegate.log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) {
            println(tag + ": " + message);
            printException(exception);
        }
        delegate.log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        if (logLevel >= LOG_ERROR) {
            errorPrintln(tag + ": " + message);
        }
        delegate.error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) {
            errorPrintln(tag + ": " + message);
            errorPrintException(exception);
        }
        delegate.error(tag, message, exception);
    }

    private void println(String text) {
        cli.getOut().println(text);
    }

    private void errorPrintln(String text) {
        cli.getErr().println(text);
    }

    private void printException(Throwable t) {
        cli.getOut().printThrowable(t);
    }

    private void errorPrintException(Throwable t) {
        cli.getErr().printThrowable(t);
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        delegate.setLogLevel(logLevel);
    }

    @Override
    public int getLogLevel() {
        return delegate.getLogLevel();
    }

    @Override
    public ApplicationType getType() {
        return delegate.getType();
    }

    @Override
    public int getVersion() {
        return delegate.getVersion();
    }

    @Override
    public long getJavaHeap() {
        return delegate.getJavaHeap();
    }

    @Override
    public long getNativeHeap() {
        return delegate.getNativeHeap();
    }

    @Override
    public Preferences getPreferences(String name) {
        return delegate.getPreferences(name);
    }

    @Override
    public Clipboard getClipboard() {
        return delegate.getClipboard();
    }

    @Override
    public void postRunnable(Runnable runnable) {
        delegate.postRunnable(runnable);
    }

    @Override
    public void exit() {
        delegate.exit();
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        delegate.addLifecycleListener(listener);
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        delegate.removeLifecycleListener(listener);
    }
}

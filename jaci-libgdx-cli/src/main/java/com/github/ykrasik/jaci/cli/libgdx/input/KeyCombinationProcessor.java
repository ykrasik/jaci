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

package com.github.ykrasik.jaci.cli.libgdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * Performs a single action when a key combination (which may also just be a single key) is detected.
 * The action is performed on keyDown().
 *
 * @author Yevgeny Krasik
 */
public abstract class KeyCombinationProcessor extends InputAdapter {
    private int keycode;
    private boolean shift;
    private boolean ctrl;
    private boolean alt;

    private boolean fired;

    /**
     * The action is fired on keyDown(), however a keyTyped() call will also be generated,
     * which we must consume - this key was never intended to be typed.
     */
    private boolean consumeNextKeyTyped;

    /**
     * Create a key combination with the given keycode.
     * Meta keys can be enabled with {@link #shift()}, {@link #ctrl()}, {@link #alt()}.
     *
     * @param keycode Base keycode for this combination.
     * @see Keys
     */
    public KeyCombinationProcessor(int keycode) {
        this.keycode = keycode;
    }

    /**
     * @return Combination keycode.
     */
    public int getKeycode() {
        return keycode;
    }

    /**
     * @param keycode keycode for this key combination.
     */
    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    /**
     * @return {@code true} if this key combination includes the {@code shift} key.
     */
    public boolean isShift() {
        return shift;
    }

    /**
     * @param shift Whether this key combination should include the {@code shift} key.
     * @return {@code this}, for chaining.
     */
    public KeyCombinationProcessor setShift(boolean shift) {
        this.shift = shift;
        return this;
    }

    /**
     * Shortcut for {@link #setShift(boolean)} that enables {@code shift} for this key combination.
     *
     * @return {@code this}, for chaining.
     */
    public KeyCombinationProcessor shift() {
        return setShift(true);
    }

    /**
     * @return {@code true} if this key combination includes the {@code ctrl} key.
     */
    public boolean isCtrl() {
        return ctrl;
    }

    /**
     * @param ctrl Whether this key combination should include the {@code ctrl} key.
     * @return {@code this}, for chaining.
     */
    public KeyCombinationProcessor setCtrl(boolean ctrl) {
        this.ctrl = ctrl;
        return this;
    }

    /**
     * Shortcut for {@link #setCtrl(boolean)} that enables {@code ctrl} for this key combination.
     *
     * @return {@code this}, for chaining.
     */
    public KeyCombinationProcessor ctrl() {
        return setCtrl(true);
    }

    /**
     * @return {@code true} if this key combination includes the {@code alt} key.
     */
    public boolean isAlt() {
        return alt;
    }

    /**
     * @param alt Whether this key combination should include the {@code alt} key.
     * @return {@code this}, for chaining.
     */
    public KeyCombinationProcessor setAlt(boolean alt) {
        this.alt = alt;
        return this;
    }

    /**
     * Shortcut for {@link #setAlt(boolean)} that enables {@code alt} for this key combination.
     *
     * @return {@code this}, for chaining.
     */
    public KeyCombinationProcessor alt() {
        return setAlt(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (fired || keycode != this.keycode) {
            return false;
        }

        if (!checkMetaKey(shift, Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT)) {
            return false;
        }
        if (!checkMetaKey(ctrl, Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT)) {
            return false;
        }
        if (!checkMetaKey(alt, Keys.ALT_LEFT, Keys.ALT_RIGHT)) {
            return false;
        }

        consumeNextKeyTyped = true;

        fired = true;
        fire();
        return true;
    }

    private boolean checkMetaKey(boolean expected, int leftKeycode, int rightKeycode) {
        return !expected || isKeyPressed(leftKeycode) || isKeyPressed(rightKeycode);
    }

    private boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!fired) {
            return false;
        }

        if (keycode == this.keycode) {
            fired = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (consumeNextKeyTyped) {
            // Consume this event to stop it from being propagated.
            consumeNextKeyTyped = false;
            return true;
        }
        return false;
    }

    /**
     * Action to be performed when this key combination is detected.
     */
    protected abstract void fire();
}

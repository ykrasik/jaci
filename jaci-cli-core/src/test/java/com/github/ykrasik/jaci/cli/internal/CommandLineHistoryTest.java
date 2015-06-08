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

package com.github.ykrasik.jaci.cli.internal;

import com.github.ykrasik.jaci.cli.commandline.CommandLineHistory;
import com.github.ykrasik.jaci.util.opt.Opt;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Yevgeny Krasik
 */
public class CommandLineHistoryTest {
    private CommandLineHistory history;

    @Before
    public void setUp() throws Exception {
        this.history = new CommandLineHistory(3);
    }

    @Test
    public void testEmptyHistory() {
        assertNoPrev();
        assertNoNext();
    }

    @Test
    public void testSingleHistory() {
        final String first = "first";
        add(first);

        // Gets 'stuck' at the first one.
        assertPrev(first);
        assertPrev(first);

        // Gets 'stuck' at the last one.
        assertNext(first);
        assertNext(first);
    }

    @Test
     public void testFirstEntryNext() {
        // Doesn't matter if we ask for the next or prev entry.
        final String first = "first";
        final String second = "second";

        add(first);
        add(second);

        assertNext(second);
    }

    @Test
    public void testSecondEntryNext() {
        // Doesn't matter if we ask for the next or prev entry.
        final String first = "first";
        final String second = "second";

        add(first);
        add(second);

        assertPrev(second);
    }

    @Test
    public void testTwoHistory() {
        final String first = "first";
        final String second = "second";

        add(first);
        add(second);

        assertPrev(second);
        assertNext(second);
        assertPrev(first);
        assertPrev(first);
        assertNext(second);
        assertNext(second);
    }

    @Test
    public void testThreeHistory() {
        final String first = "first";
        final String second = "second";
        final String third = "third";

        add(first);
        add(second);
        add(third);

        assertNext(third);
        assertPrev(second);
        assertNext(third);
        assertPrev(second);
        assertPrev(first);

        assertPrev(first);
        assertNext(second);
        assertPrev(first);
        assertNext(second);
        assertNext(third);
        assertNext(third);
    }

    @Test
    public void testHistoryLimit() {
        // Limit is 3, add 4 entries.
        final String first = "first";
        final String second = "second";
        final String third = "third";
        final String fourth = "fourth";

        add(first);
        add(second);
        add(third);
        add(fourth);

        assertNext(fourth);
        assertPrev(third);
        assertNext(fourth);
        assertPrev(third);
        assertPrev(second);
        assertPrev(second);

        assertNext(third);
        assertPrev(second);
        assertNext(third);
        assertNext(fourth);
        assertNext(fourth);
    }

    @Test
    public void testAddWhileIteratingHistory() {
        // Adding a new history entry while iterating the history resets the iterator.
        final String first = "first";
        final String second = "second";
        final String third = "third";

        add(first);
        add(second);
        add(third);

        assertPrev(third);
        assertPrev(second);

        final String fourth = "fourth";
        add(fourth);

        assertPrev(fourth);
        assertPrev(third);
        assertNext(fourth);
        assertPrev(third);
        assertPrev(second);

        final String fifth = "fifth";
        add(fifth);

        assertNext(fifth);
        assertNext(fifth);
        assertPrev(fourth);
        assertNext(fifth);
        assertPrev(fourth);
        assertPrev(third);
        assertPrev(third);
        assertNext(fourth);
        assertNext(fifth);
        assertNext(fifth);
    }

    private void add(String commandLine) {
        history.pushCommandLine(commandLine);
    }

    private void assertNext(String commandLine) {
        doAssert(history.getNextCommandLine(), commandLine);
    }

    private void assertPrev(String commandLine) {
        doAssert(history.getPrevCommandLine(), commandLine);
    }

    private void doAssert(Opt<String> commandLine, String expectedCommandLine) {
        assertTrue(commandLine.isPresent());
        assertEquals(expectedCommandLine, commandLine.get());
    }

    private void assertNoNext() {
        assertFalse(history.getNextCommandLine().isPresent());
    }

    private void assertNoPrev() {
        assertFalse(history.getPrevCommandLine().isPresent());
    }
}

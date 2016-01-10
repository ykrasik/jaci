/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 * *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.cli.commandline;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Yevgeny Krasik
 */
public class CommandLineTest {
    private List<String> expected;

    @Test
    public void startsWithSpace() {
        setExpected("test");
        assertExpected(" test");
        assertExpected("  test");
        assertExpected("   test");
    }

    @Test
    public void endsWithSpace() {
        setExpected("test");
        assertExpected("test ");
        assertExpected("test  ");
        assertExpected("test   ");
    }

    @Test
    public void intermediateSpaces() {
        setExpected("test1", "test2");
        assertExpected("test1 test2");
        assertExpected("test1  test2");
        assertExpected("test1   test2");
    }

    @Test
    public void mixedSpaces() {
        setExpected("test1", "test2");
        assertExpected(" test1 test2 ");
        assertExpected("  test1  test2  ");
        assertExpected("   test1   test2   ");
    }

    @Test
    public void quotedWord() {
        setExpected("quoted");
        assertExpected("'quoted'");
        assertExpected(" 'quoted' ");
        assertExpected("  'quoted'  ");
    }

    @Test
    public void quotedText() {
        setExpected("quoted text");
        assertExpected("'quoted text'");
        assertExpected(" 'quoted text' ");
        assertExpected("  'quoted text'  ");

        setExpected("quoted  text");
        assertExpected("'quoted  text'");
        assertExpected(" 'quoted  text' ");
        assertExpected("  'quoted  text'  ");

        setExpected("quoted   text");
        assertExpected("'quoted   text'");
        assertExpected(" 'quoted   text' ");
        assertExpected("  'quoted   text'  ");
    }

    @Test
    public void quotedUnquotedText() {
        setExpected("unquoted", "quoted text");
        assertExpected("unquoted 'quoted text'");
        assertExpected(" unquoted 'quoted text' ");
        assertExpected("  unquoted  'quoted text'  ");

        setExpected("unquoted", "text", "quoted text", "more quoted");
        assertExpected("unquoted text 'quoted text' 'more quoted'");
        assertExpected(" unquoted text 'quoted text' 'more quoted' ");
        assertExpected("  unquoted  text  'quoted text'  'more quoted'  ");

        setExpected("unquoted", "text", "quoted  text", "u", "more   quoted", "unquoted", "again");
        assertExpected("unquoted  text  'quoted  text'  u  'more   quoted'  unquoted  again");
        assertExpected(" unquoted  text  'quoted  text'  u  'more   quoted'  unquoted  again ");
        assertExpected("  unquoted  text  'quoted  text'  u  'more   quoted'  unquoted  again  ");
    }

    @Test
    public void noWordBoundaryBeforeQuote() {
        setExpected("unquoted'text");
        assertExpected("unquoted'text");
        assertExpected(" unquoted'text ");
        assertExpected("  unquoted'text  ");

        setExpected("unquoted'text", "more", "text'");
        assertExpected("unquoted'text more text'");
        assertExpected(" unquoted'text more text' ");
        assertExpected("  unquoted'text  more  text'  ");
    }

    @Test
    public void unterminatedQuote() {
        setExpected("unquoted", "");
        assertExpected("unquoted '");

        setExpected("unquoted", " ");
        assertExpected(" unquoted ' ");

        setExpected("unquoted", "  ");
        assertExpected("  unquoted  '  ");
    }

    @Test
    public void emptyQuote() {
        setExpected("");
        assertExpected("''");
        assertExpected(" '' ");
        assertExpected("  ''  ");

        setExpected("unquoted", "", "unquoted2");
        assertExpected("unquoted '' unquoted2");
        assertExpected(" unquoted '' unquoted2 ");
        assertExpected("  unquoted  ''  unquoted2  ");
    }

    @Test
    public void mixedQuotes() {
        setExpected("quoted", "unquoted", "quoted2");
        assertExpected("'quoted' unquoted \"quoted2\"");
        assertExpected(" 'quoted' unquoted \"quoted2\" ");
        assertExpected("  'quoted'  unquoted  \"quoted2\"  ");
        assertExpected("\"quoted\" unquoted 'quoted2'");
        assertExpected(" \"quoted\" unquoted 'quoted2' ");
        assertExpected("  \"quoted\"  unquoted  'quoted2'  ");
    }

    @Test
    public void nestedQuotes() {
        setExpected("quoted \"nested quote\" outer quote");
        assertExpected("'quoted \"nested quote\" outer quote'");
        assertExpected(" 'quoted \"nested quote\" outer quote' ");
        assertExpected("  'quoted \"nested quote\" outer quote'  ");

        setExpected("quoted 'nested quote' outer quote");
        assertExpected("\"quoted 'nested quote' outer quote\"");
        assertExpected(" \"quoted 'nested quote' outer quote\" ");
        assertExpected("  \"quoted 'nested quote' outer quote\"  ");
    }

    @Test
    public void mixedNestedQuotes() {
        setExpected("unquoted", "quoted \"nested quote\" outer quote", "unquoted");
        assertExpected("unquoted 'quoted \"nested quote\" outer quote' unquoted");

        setExpected("unquoted", "quoted  \"nested  quote\"  outer  quote", "unquoted");
        assertExpected(" unquoted 'quoted  \"nested  quote\"  outer  quote' unquoted ");
        assertExpected("  unquoted  'quoted  \"nested  quote\"  outer  quote'  unquoted  ");

        setExpected("unquoted", "quoted 'nested quote' outer quote", "unquoted");
        assertExpected("unquoted \"quoted 'nested quote' outer quote\" unquoted");

        setExpected("unquoted", "quoted  'nested  quote'  outer  quote", "unquoted");
        assertExpected(" unquoted \"quoted  'nested  quote'  outer  quote\" unquoted ");
        assertExpected("  unquoted  \"quoted  'nested  quote'  outer  quote\"  unquoted  ");
    }

    private void setExpected(String... expected) {
        this.expected = Arrays.asList(expected);
    }

    private void assertExpected(String commandLine) {
        final List<String> parsed = CommandLine.splitCommandLine(commandLine);
        assertEquals(expected, parsed);
    }
}

/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jerminal.collections.trie;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Yevgeny Krasik
 */
public class TriePrefixTest extends AbstractTrieTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();

        buildTrie("p", "pr", "pre", "pre1fix", "pre2", "prefix", "prefixprefix1", "prefixprefix2", "prefiz", "other");
    }

    @Test
    public void testPrefix1() {
        // Root
        assertWords("p", "pr", "pre", "pre1fix", "pre2", "prefix", "prefixprefix1", "prefixprefix2", "prefiz", "other");
        assertLongestPrefix("");
    }

    @Test
    public void testPrefix2() {
        // "p"
        successfulSubTrie("p");
        assertWords("p", "pr", "pre", "pre1fix", "pre2", "prefix", "prefixprefix1", "prefixprefix2", "prefiz");
        assertLongestPrefix("p");
    }

    @Test
    public void testPrefix3() {
        // "pr"
        successfulSubTrie("pr");
        assertWords("pr", "pre", "pre1fix", "pre2", "prefix", "prefixprefix1", "prefixprefix2", "prefiz");
        assertLongestPrefix("pr");
    }

    @Test
    public void testPrefix4() {
        // "pre"
        successfulSubTrie("pre");
        assertWords("pre", "pre1fix", "pre2", "prefix", "prefixprefix1", "prefixprefix2", "prefiz");
        assertLongestPrefix("pre");
    }

    @Test
    public void testPrefix5() {
        // "pre1" - only "pre1fix" is possible from here.
        successfulSubTrie("pre1");
        assertWords("pre1fix");
        assertLongestPrefix("pre1fix");
    }

    @Test
    public void testPrefix6() {
        // "pre2" - only "pre2" is possible from here.
        successfulSubTrie("pre2");
        assertWords("pre2");
        assertLongestPrefix("pre2");
    }

    @Test
    public void testPrefix7() {
        // "pref"
        successfulSubTrie("pref");
        assertWords("prefix", "prefixprefix1", "prefixprefix2", "prefiz");
        assertLongestPrefix("prefi");
    }

    @Test
    public void testPrefix8() {
        // "prefi"
        successfulSubTrie("prefi");
        assertWords("prefix", "prefixprefix1", "prefixprefix2", "prefiz");
        assertLongestPrefix("prefi");
    }

    @Test
    public void testPrefix9() {
        // "prefix"
        successfulSubTrie("prefix");
        assertWords("prefix", "prefixprefix1", "prefixprefix2");
        assertLongestPrefix("prefix");
    }

    @Test
    public void testPrefix10() {
        // "prefixp"
        successfulSubTrie("prefixp");
        assertWords("prefixprefix1", "prefixprefix2");
        assertLongestPrefix("prefixprefix");
    }

    @Test
    public void testPrefix11() {
        // "prefiz"
        successfulSubTrie("prefiz");
        assertWords("prefiz");
        assertLongestPrefix("prefiz");
    }

    @Test
    public void testFailedPrefix() {
        // Invalid prefixes
        failedSubTrie("prefix1");
        failedSubTrie("prefixprex");
        failedSubTrie("pred");
        failedSubTrie("predix");
        failedSubTrie("ob");
        failedSubTrie("othar");
        failedSubTrie("others");
    }
}

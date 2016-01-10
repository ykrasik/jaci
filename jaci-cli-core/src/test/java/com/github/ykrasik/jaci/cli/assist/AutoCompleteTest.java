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

import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.trie.Trie;
import com.github.ykrasik.jaci.util.trie.TrieBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Yevgeny Krasik
 */
public class AutoCompleteTest {
    private String prefix;
    private List<String> words;

    @Test
    public void testEmptyAutoCompleteSuffix() throws Exception {
        setPrefix("");
        setWords();
        assertNoAutoCompleteSuffix();
    }

    @Test
    public void testSingleAutoCompleteSuffix() throws Exception {
        // When only a single possibility -
        // suffix should be the difference between the prefix and the possibility + the CliValueType's suffix.
        setPrefix("pre");
        setWords("prefix");
        assertAutoCompleteSuffix("fix" + CliValueType.DIRECTORY.getSuffix());
    }

    @Test
    public void testMultipleAutoCompleteSuffix() throws Exception {
        // When multiple possibilities - suffix should be longest available prefix.
        setPrefix("pre");
        setWords("prefix1", "prefix2");
        assertAutoCompleteSuffix("fix");
    }

    @Test
    public void testMultipleAutoCompleteEmptySuffix() throws Exception {
        // When multiple possibilities - suffix should be longest available prefix.
        // When longest available prefix is empty, should be no auto complete.
        setPrefix("pre");
        setWords("pre1", "pre2");
        assertNoAutoCompleteSuffix();
    }

    @Test
    public void testSuggestions() throws Exception {
        // TODO: Implement
    }

    @Test
    public void testUnion() throws Exception {
        // TODO: Implement
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setWords(String... words) {
        this.words = Arrays.asList(words);
    }

    private void assertAutoCompleteSuffix(String expected) {
        final Opt<String> autoCompleteSuffix = processGetAutoCompleteSuffix();
        assertTrue(autoCompleteSuffix.isPresent());
        assertEquals(expected, autoCompleteSuffix.get());
    }

    private void assertNoAutoCompleteSuffix() {
        final Opt<String> autoCompleteSuffix = processGetAutoCompleteSuffix();
        assertFalse(autoCompleteSuffix.isPresent());
    }

    private Opt<String> processGetAutoCompleteSuffix() {
        final AutoComplete autoComplete = buildAutoComplete();
        return autoComplete.getAutoCompleteSuffix();
    }

    private AutoComplete buildAutoComplete() {
        final TrieBuilder<CliValueType> builder = new TrieBuilder<>();
        for (String word : words) {
            builder.add(word, CliValueType.DIRECTORY);
        }
        final Trie<CliValueType> possibilities = builder.build();
        return new AutoComplete(prefix, possibilities);
    }
}

//package com.github.ykrasik.jerminal.old.parameter;
//
//import com.github.ykrasik.jemi.util.trie.Trie;
//import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;
//import com.github.ykrasik.jerminal.internal.assist.AutoCompleteType;
//import com.github.ykrasik.jemi.cli.exception.ParseException;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
///**
// * @author Yevgeny Krasik
// */
//public class BaseParamTest<T> {
//    protected CommandParam param;
//
//    protected void parse(String str, T expectedValue) {
//        final T value = doParse(str);
//        assertEquals(expectedValue, value);
//    }
//
//    @SuppressWarnings("unchecked")
//    private T doParse(String str) {
//        try {
//            return (T) param.parse(str);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected void parseInvalid(String str) {
//        try {
//            param.parse(str);
//            fail();
//        } catch (ParseException ignored) {
//
//        }
//    }
//
//    protected void autoComplete(String str, String... expectedWords) {
//        final Trie<AutoCompleteType> possibilities = doAutoComplete(str);
//        final Collection<String> words = possibilities.words();
//        final List<String> expected = Arrays.asList(expectedWords);
//        for (String word : words) {
//            assertTrue(expected.contains(word));
//        }
//        assertEquals(words.size(), expected.size());
//    }
//
//    private Trie<AutoCompleteType> doAutoComplete(String str) {
//        try {
//            final AutoCompleteReturnValue returnValue = param.autoComplete(str);
//            return returnValue.getPossibilities();
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    protected void autoCompleteInvalid(String str) {
//        try {
//            param.autoComplete(str);
//            fail();
//        } catch (ParseException ignored) {
//
//        }
//    }
//
//    protected void autoCompleteEmpty(String str) {
//        autoComplete(str);
//    }
//}

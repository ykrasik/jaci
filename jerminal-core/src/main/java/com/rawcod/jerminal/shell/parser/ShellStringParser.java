package com.rawcod.jerminal.shell.parser;

import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.collections.trie.TrieFilter.NoTrieFilter;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.filesystem.entry.ShellAutoComplete;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ShellStringParser<V> {
    private static final TrieFilter NO_FILTER = new NoTrieFilter<>();

    private final String autoCompleteErrorFormat;
    private final String parseErrorFormat;
    private final ShellWordContainer<V> wordContainer;

    public ShellStringParser(String autoCompleteErrorFormat, String parseErrorFormat) {
        this.autoCompleteErrorFormat = autoCompleteErrorFormat;
        this.parseErrorFormat = parseErrorFormat;
        this.wordContainer = new ShellWordContainer<>();
    }

    public void addWord(String name, V value) {
        wordContainer.addWord(name, value);
    }

    public boolean isEmpty() {
        return wordContainer.isEmpty();
    }

    public List<V> getAllValues() {
        return wordContainer.getAllValues();
    }

    public ParseReturnValue<V> parse(String arg) {
        return parse(arg, NO_FILTER);
    }

    public ParseReturnValue<V> parse(String arg, TrieFilter<V> filter) {
        final V entry = wordContainer.get(arg);
        if (entry != null && !filter.shouldFilter(entry)) {
            return ParseReturnValue.success(entry);
        }

        final AutoCompleteReturnValue returnValue = doAutoComplete(arg, filter, true);
        return ParseReturnValue.failure(returnValue.getErrorCode(),
            returnValue.getErrorMessage(),
            returnValue.getAutoComplete());
    }

    public AutoCompleteReturnValue autoComplete(String arg) {
        return autoComplete(arg, NO_FILTER);
    }

    public AutoCompleteReturnValue autoComplete(String arg, TrieFilter<V> filter) {
        return doAutoComplete(arg, filter, false);
    }

    private AutoCompleteReturnValue doAutoComplete(String arg, TrieFilter<V> filter, boolean eager) {
        final ShellAutoComplete autoComplete = wordContainer.autoComplete(arg, filter);
        final List<String> suggestions = autoComplete.getSuggestions();

        // Couldn't match any child entry.
        if (suggestions.isEmpty()) {
            final String errorMessage = String.format(autoCompleteErrorFormat, arg);
            return AutoCompleteReturnValue.failureInvalidArgument(errorMessage, autoComplete);
        } else {
            if (!eager) {
                // Non-eager parsing - can be autoCompleted
                return AutoCompleteReturnValue.success(autoComplete);
            } else {
                // Eager parsing - it was expected to find an entry named 'arg' (exactly)
                final String errorMessage = String.format(parseErrorFormat, arg);
                return AutoCompleteReturnValue.failureInvalidArgument(errorMessage, autoComplete);
            }
        }
    }
}

package com.rawcod.jerminal.shell.parser;

import com.rawcod.jerminal.collections.trie.TrieFilter;
import com.rawcod.jerminal.collections.trie.TrieFilter.NoTrieFilter;
import com.rawcod.jerminal.shell.entry.ShellAutoComplete;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

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

    public ShellParseReturnValue<V> parse(String arg) {
        return parse(arg, NO_FILTER);
    }

    public ShellParseReturnValue<V> parse(String arg, TrieFilter<V> filter) {
        final V entry = wordContainer.get(arg);
        if (entry != null && !filter.shouldFilter(entry)) {
            return ShellParseReturnValue.success(entry);
        }

        final ShellAutoCompleteReturnValue returnValue = doAutoComplete(arg, filter, true);
        return ShellParseReturnValue.failure(returnValue.getErrorCode(),
                                             returnValue.getErrorMessage(),
                                             returnValue.getAutoComplete());
    }

    public ShellAutoCompleteReturnValue autoComplete(String arg) {
        return autoComplete(arg, NO_FILTER);
    }

    public ShellAutoCompleteReturnValue autoComplete(String arg, TrieFilter<V> filter) {
        return doAutoComplete(arg, filter, false);
    }

    private ShellAutoCompleteReturnValue doAutoComplete(String arg, TrieFilter<V> filter, boolean eager) {
        final ShellAutoComplete autoComplete = wordContainer.autoComplete(arg, filter);
        final List<String> suggestions = autoComplete.getSuggestions();

        // Couldn't match any child entry.
        if (suggestions.isEmpty()) {
            final String errorMessage = String.format(autoCompleteErrorFormat, arg);
            return ShellAutoCompleteReturnValue.failureInvalidArgument(errorMessage, autoComplete);
        } else {
            if (!eager) {
                // Non-eager parsing - can be autoCompleted
                return ShellAutoCompleteReturnValue.success(autoComplete);
            } else {
                // Eager parsing - it was expected to find an entry named 'arg' (exactly)
                final String errorMessage = String.format(parseErrorFormat, arg);
                return ShellAutoCompleteReturnValue.failureInvalidArgument(errorMessage, autoComplete);
            }
        }
    }
}

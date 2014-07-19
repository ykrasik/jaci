package com.rawcod.jerminal.filesystem.entry.parameters.string.provider;

import com.rawcod.jerminal.shell.parser.ShellStringParser;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/01/14
 */
public abstract class DynamicStringValueProvider extends AbstractStringValueProvider {
    public DynamicStringValueProvider(String name) {
        super(name);
    }

    @Override
    protected final ShellStringParser<String> getPossibleValues() {
        final String autoCompleteErrorFormat = String.format("Param '%s' can't take any values starting with", name) + " '%s'";
        final String parseErrorFormat = String.format("Invalid value for param '%s':", name) + " '%s'";
        final ShellStringParser<String> possibleValues = new ShellStringParser<>(autoCompleteErrorFormat, parseErrorFormat);
        final List<String> dynamicPossibleValues = getDynamicPossibleValues();
        for (String possibleValue : dynamicPossibleValues) {
            if (possibleValue.isEmpty()) {
                throw new RuntimeException("String params cannot have empty possible values!");
            }
            possibleValues.addWord(possibleValue, possibleValue);
        }
        return possibleValues;
    }

    protected abstract List<String> getDynamicPossibleValues();
}

package com.rawcod.jerminal.filesystem.entry.parameters.string.provider;

import com.rawcod.jerminal.shell.parser.ShellStringParser;

import java.util.List;

/**
* User: ykrasik
* Date: 25/01/14
*/
public class ConstStringValueProvider extends AbstractStringValueProvider {
    private final ShellStringParser<String> possibleValues;

    public ConstStringValueProvider(String name, List<String> possibleValues) {
        super(name);
        final String autoCompleteErrorFormat = String.format("Param '%s' can't take any values starting with", name) + " '%s'";
        final String parseErrorFormat = String.format("Invalid value for param '%s':", name) + " '%s'";
        this.possibleValues = new ShellStringParser<>(autoCompleteErrorFormat, parseErrorFormat);
        for (String possibleValue : possibleValues) {
            if (possibleValue.isEmpty()) {
                throw new RuntimeException("String params cannot have empty possible values!");
            }
            this.possibleValues.addWord(possibleValue, possibleValue);
        }
    }

    @Override
    protected ShellStringParser<String> getPossibleValues() {
        return possibleValues;
    }
}

package com.rawcod.jerminal.returnvalue.parse;

import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.returnvalue.parse.args.ParseBoundParamsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 17:26
 */
public final class ParseErrors {
    private ParseErrors() {
    }

    public static ParseEntryReturnValue emptyDirectory(String directoryName) {
        return ParseEntryReturnValue.failure(from(
            ParseError.EMPTY_DIRECTORY,
            "Parse error: Directory '%s' is empty.", directoryName
        ));
    }

    public static ParseEntryReturnValue entryDoesNotExist(String directoryName, String entry) {
        return ParseEntryReturnValue.failure(from(
            ParseError.ENTRY_DOES_NOT_EXIST,
            "Parse error: Directory '%s' doesn't contain entry '%s'.", directoryName, entry
        ));
    }

    public static ParseReturnValueFailure invalidAccessToEntry(String directoryName, String entry) {
        return from(
            ParseError.INVALID_ACCESS_TO_ENTRY,
            "Parse error: Invalid access from directory '%s' to entry '%s'.", directoryName, entry
        );
    }

    public static ParseParamReturnValue invalidParam(String paramName) {
        return ParseParamReturnValue.failure(from(
            ParseError.INVALID_PARAM,
            "Parse error: Invalid param: '%s'", paramName
        ));
    }

    public static ParseReturnValueFailure invalidParamValue(String paramName, String value) {
        return from(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Invalid value for param '%s': '%s'.", paramName, value
        );
    }

    public static ParseReturnValueFailure invalidFlagValue(String paramName) {
        return from(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Flag params take no value: '%s'.", paramName
        );
    }

    public static ParseReturnValueFailure paramsNotBound(Collection<CommandParam> params) {
        return from(
            ParseError.PARAM_NOT_BOUND,
            "Parse error: Mandatory parameters have not been bound: %s", params
        );
    }

    public static ParseBoundParamsReturnValue paramAlreadyBound(String paramName, Object value) {
        return ParseBoundParamsReturnValue.failure(from(
            ParseError.PARAM_ALREADY_BOUND,
            "Parse error: Param '%s' is already bound to a value: '%s", paramName, value
        ));
    }

    public static ParseReturnValueFailure internalError(String format, Object... args) {
        return from(
            ParseError.INTERNAL_ERROR,
            "Internal error: " + format, args
        );
    }

    private static ParseReturnValueFailure from(ParseError error, String format, Object... args) {
        return from(error, Collections.<String>emptyList(), format, args);
    }

    private static ParseReturnValueFailure from(ParseError error, List<String> suggestions, String format, Object... args) {
        final String message = String.format(format, args);
        return new ParseReturnValueFailure(error, message, suggestions);
    }
}
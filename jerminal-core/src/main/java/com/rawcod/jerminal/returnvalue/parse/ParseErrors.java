package com.rawcod.jerminal.returnvalue.parse;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.parse.args.ParseBoundParamsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;
import com.rawcod.jerminal.returnvalue.suggestion.Suggestions;

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

    public static ParseEntryReturnValue directoryDoesNotContainEntry(String directoryName, String entry, boolean directory) {
        final String entryType = directory ? "directory" : "command";
        return ParseEntryReturnValue.failure(from(
            ParseError.INVALID_ENTRY,
            "Parse error: Directory '%s' doesn't contain %s '%s'", directoryName, entryType, entry
        ));
    }

    public static ParseEntryReturnValue directoryDoesNotHaveParent(String directoryName) {
        return ParseEntryReturnValue.failure(from(
            ParseError.INVALID_ENTRY,
            "Parse error: Directory '%s' doesn't have a parent.", directoryName
        ));
    }

    public static ParseEntryReturnValue invalidAccessToEntry(String entry, boolean directory) {
        final String desiredEntryType = directory ? "directory" : "command";
        final String actualEntryType = directory ? "command" : "directory";
        return ParseEntryReturnValue.failure(from(
            ParseError.INVALID_ACCESS_TO_ENTRY,
            "Parse error: '%s' is a %s, not a %s!", entry, actualEntryType, desiredEntryType
        ));
    }

    public static ParseParamReturnValue invalidParam(String paramName) {
        return ParseParamReturnValue.failure(from(
            ParseError.INVALID_PARAM,
            "Parse error: Invalid param: '%s'", paramName
        ));
    }

    public static ParseParamValueReturnValue invalidParamValue(String paramName, String value) {
        return ParseParamValueReturnValue.failure(from(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Invalid value for param '%s': '%s'", paramName, value
        ));
    }

    public static ParseParamValueReturnValue invalidFlagValue(String paramName) {
        return ParseParamValueReturnValue.failure(from(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Flag params take no value: '%s'", paramName
        ));
    }

    public static ParseParamValueReturnValue paramNotBound(String paramName) {
        return ParseParamValueReturnValue.failure(from(
            ParseError.PARAM_NOT_BOUND,
            "Parse error: Mandatory parameter was not bound: '%s'", paramName
        ));
    }

    public static ParseBoundParamsReturnValue paramAlreadyBound(String paramName, Object value) {
        return ParseBoundParamsReturnValue.failure(from(
            ParseError.PARAM_ALREADY_BOUND,
            "Parse error: Param '%s' is already bound to a value: '%s'", paramName, value
        ));
    }

    public static ParseParamReturnValue noMoreParams() {
        return ParseParamReturnValue.failure(from(
            ParseError.NO_MORE_PARAMS,
            "Parse error: Command does not accept any more parameters."
        ));
    }

    public static ParseReturnValueFailure internalError(String format, Object... args) {
        return from(
            ParseError.INTERNAL_ERROR,
            "Internal error: " + format, args
        );
    }

    private static ParseReturnValueFailure from(ParseError error, String format, Object... args) {
        return from(error, Optional.<Suggestions>absent(), format, args);
    }

    private static ParseReturnValueFailure from(ParseError error, Optional<Suggestions> suggestions, String format, Object... args) {
        final String message = String.format(format, args);
        return new ParseReturnValueFailure(error, message, suggestions);
    }
}

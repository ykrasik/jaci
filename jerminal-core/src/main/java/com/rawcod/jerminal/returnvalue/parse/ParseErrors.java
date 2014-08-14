package com.rawcod.jerminal.returnvalue.parse;

import com.rawcod.jerminal.exception.ParseException;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 17:26
 */
public final class ParseErrors {
    private ParseErrors() {
    }

    public static ParseException emptyDirectory(String directoryName) {
        return new ParseException(
            ParseError.EMPTY_DIRECTORY,
            "Parse error: Directory '%s' is empty.", directoryName
        );
    }

    public static ParseException directoryDoesNotContainEntry(String directoryName, String entry, boolean directory) {
        final String entryType = directory ? "directory" : "command";
        return new ParseException(
            ParseError.INVALID_ENTRY,
            "Parse error: Directory '%s' doesn't contain %s '%s'", directoryName, entryType, entry
        );
    }

    public static ParseException directoryDoesNotHaveParent(String directoryName) {
        return new ParseException(
            ParseError.INVALID_ENTRY,
            "Parse error: Directory '%s' doesn't have a parent.", directoryName
        );
    }

    public static ParseException invalidAccessToEntry(String entry, boolean directory) {
        final String desiredEntryType = directory ? "directory" : "command";
        final String actualEntryType = directory ? "command" : "directory";
        return new ParseException(
            ParseError.INVALID_ACCESS_TO_ENTRY,
            "Parse error: '%s' is a %s, not a %s!", entry, actualEntryType, desiredEntryType
        );
    }

    public static ParseException invalidParam(String paramName) {
        return new ParseException(
            ParseError.INVALID_PARAM,
            "Parse error: Invalid parameter: '%s'", paramName
        );
    }

    public static ParseException invalidParamValue(String paramName, String value) {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Invalid value for parameter '%s': '%s'", paramName, value
        );
    }

    public static ParseException invalidFlagValue(String paramName) {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Parse error: Flag params take no value: '%s'", paramName
        );
    }

    public static ParseException paramNotBound(String paramName) {
        return new ParseException(
            ParseError.PARAM_NOT_BOUND,
            "Parse error: Mandatory parameter was not bound: '%s'", paramName
        );
    }

    public static ParseException paramAlreadyBound(String paramName, Object value) {
        return new ParseException(
            ParseError.PARAM_ALREADY_BOUND,
            "Parse error: Parameter '%s' is already bound to a value: '%s'", paramName, value
        );
    }

    public static ParseException noMoreParams() {
        return new ParseException(
            ParseError.NO_MORE_PARAMS,
            "Parse error: Command does not accept any more parameters."
        );
    }

    public static ParseException internalError(String format, Object... args) {
        return new ParseException(
            ParseError.INTERNAL_ERROR,
            "Internal error: " + format, args
        );
    }
}

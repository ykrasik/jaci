package com.rawcod.jerminal.returnvalue.autocomplete;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.parse.ParseError;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 18:56
 */
public final class AutoCompleteErrors {
    private AutoCompleteErrors() {
    }

    public static AutoCompleteReturnValue parseError(ParseReturnValueFailure failure) {
        return AutoCompleteReturnValue.failure(
            new AutoCompleteReturnValueFailure(
                AutoCompleteError.PARSE_ERROR,
                Optional.of(failure.getError()),
                failure.getErrorMessage(),
                failure.getSuggestions()
            )
        );
    }

    public static AutoCompleteReturnValue emptyDirectory(String directoryName) {
        return parseError(ParseErrors.emptyDirectory(directoryName).getFailure());
    }

    public static AutoCompleteReturnValue invalidParam(String paramName) {
        return parseError(ParseErrors.invalidParam(paramName).getFailure());
    }

    public static AutoCompleteReturnValue paramAlreadyBound(String paramName, Object value) {
        return parseError(ParseErrors.paramAlreadyBound(paramName, value).getFailure());
    }

    public static AutoCompleteReturnValue paramNotBound(String paramName) {
        return parseError(ParseErrors.paramNotBound(paramName).getFailure());
    }

    public static AutoCompleteReturnValue invalidFlagValue(String paramName) {
        return parseError(ParseErrors.invalidFlagValue(paramName).getFailure());
    }

    public static AutoCompleteReturnValue noPossibleValuesForParamNamePrefix(String prefix) {
        return AutoCompleteReturnValue.failure(
            from(
                AutoCompleteError.NO_POSSIBLE_VALUES,
                "AutoComplete error: No unbound param starts with '%s'", prefix
            )
        );
    }

    public static AutoCompleteReturnValue noPossibleValuesForNumberParam(String paramName) {
        return AutoCompleteReturnValue.failure(
            from(
                AutoCompleteError.NO_POSSIBLE_VALUES,
                "AutoComplete error: Number parameters cannot be auto completed: '%s'", paramName
            )
        );
    }

    public static AutoCompleteReturnValue noPossibleValuesForParamWithPrefix(String paramName, String prefix) {
        return AutoCompleteReturnValue.failure(
            from(
                AutoCompleteError.NO_POSSIBLE_VALUES,
                "AutoComplete error: No values are possible for param '%s' with prefix: '%s'", paramName, prefix
            )
        );
    }

    public static AutoCompleteReturnValue noPossibleValuesForDirectoryWithPrefix(String directoryName, String prefix) {
        return AutoCompleteReturnValue.failure(
            from(
                AutoCompleteError.NO_POSSIBLE_VALUES,
                "AutoComplete error: No child entries possible for directory='%s', prefix='%s'", directoryName, prefix
            )
        );
    }

    public static AutoCompleteReturnValue noPossibleValuesForPrefix(String prefix) {
        return AutoCompleteReturnValue.failure(
            from(
                AutoCompleteError.NO_POSSIBLE_VALUES,
                "AutoComplete error: No values are possible for prefix='%s'", prefix
            )
        );
    }

    public static AutoCompleteReturnValue internalError(String format, Object... args) {
        return AutoCompleteReturnValue.failure(
            from(
                AutoCompleteError.INTERNAL_ERROR,
                "Internal error: " + format, args
            )
        );
    }

    public static AutoCompleteReturnValue internalErrorEmptyPossibilities() {
        return internalError("Internal error: AutoComplete succeeded, but returned no possibilities!");
    }

    private static AutoCompleteReturnValueFailure from(AutoCompleteError error, String format, Object... args) {
        return from(error, Collections.<String>emptyList(), format, args);
    }

    private static AutoCompleteReturnValueFailure from(AutoCompleteError error,
                                                      List<String> suggestions,
                                                      String format, Object... args) {
        final String message = String.format(format, args);
        return new AutoCompleteReturnValueFailure(error, Optional.<ParseError>absent(), message, suggestions);
    }
}

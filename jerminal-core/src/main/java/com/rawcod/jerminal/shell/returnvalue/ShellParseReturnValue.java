package com.rawcod.jerminal.shell.returnvalue;

import com.rawcod.jerminal.shell.entry.ShellAutoComplete;

/**
 * User: ykrasik
 * Date: 14/01/14
 */
public class ShellParseReturnValue<V> {
    private final boolean success;
    private final V parsedValue;
    private final ShellErrorCode errorCode;
    private final String errorMessage;
    private final ShellAutoComplete autoComplete;
    private String usage;

    protected ShellParseReturnValue(boolean success,
                                    V parsedValue,
                                    ShellErrorCode errorCode,
                                    String errorMessage,
                                    ShellAutoComplete autoComplete) {
        this.success = success;
        this.parsedValue = parsedValue;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.autoComplete = autoComplete;
    }

    public boolean isSuccess() {
        return success;
    }

    public V getParsedValue() {
        return parsedValue;
    }

    public ShellErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ShellAutoComplete getAutoComplete() {
        return autoComplete;
    }

    public String getUsage() {
        return usage;
    }

    public ShellParseReturnValue<V> withUsage(String usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShellParseReturnValue{");
        sb.append("success=").append(success);
        sb.append(", parsedValue=").append(parsedValue);
        sb.append(", errorCode=").append(errorCode);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", autoComplete=").append(autoComplete);
        sb.append('}');
        return sb.toString();
    }

    public static <V> ShellParseReturnValue<V> success(V parsedValue) {
        return new ShellParseReturnValue<>(true, parsedValue, null, null, null);
    }

    public static <V> ShellParseReturnValue<V> failure(ShellErrorCode errorCode,
                                                       String errorMessage,
                                                       ShellAutoComplete autoComplete) {
        return new ShellParseReturnValue<>(false, null, errorCode, errorMessage, autoComplete);
    }

    public static <V> ShellParseReturnValue<V> failure(ShellErrorCode errorCode, String errorMessage) {
        return failure(errorCode, errorMessage, ShellAutoComplete.none());
    }

    public static <V> ShellParseReturnValue<V> failureInvalidCommand(String errorMessage, ShellAutoComplete autoComplete) {
        return failure(ShellErrorCode.INVALID_COMMAND, errorMessage, autoComplete);
    }

    public static <V> ShellParseReturnValue<V> failureInvalidArgument(String errorMessage) {
        return failure(ShellErrorCode.INVALID_ARGUMENT, errorMessage);
    }

    public static <V> ShellParseReturnValue<V> failureInvalidArgument(String errorMessage,
                                                                      ShellAutoComplete autoComplete) {
        return failure(ShellErrorCode.INVALID_ARGUMENT, errorMessage, autoComplete);
    }

    public static <V> ShellParseReturnValue<V> failureMissingArgument(String argName) {
        final String errorMessage = String.format("Argument not provided: '%s'", argName);
        return failure(ShellErrorCode.MISSING_ARGUMENT, errorMessage);
    }

    public static <V> ShellParseReturnValue<V> failureExcessArgument(Object excessArg) {
        final String errorMessage = String.format("Excess arguments provided: %s", excessArg);
        return failure(ShellErrorCode.EXCESS_ARGUMENT, errorMessage);
    }

    public static <V> ShellParseReturnValue<V> failureInternalError(String errorMessage) {
        return failure(ShellErrorCode.INTERNAL_ERROR, errorMessage);
    }

    public static <F, T> ShellParseReturnValue<T> failureFrom(ShellParseReturnValue<F> returnValue) {
        return failure(returnValue.errorCode,
                       returnValue.errorMessage,
                       returnValue.autoComplete);
    }
}
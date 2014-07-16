package com.rawcod.jerminal.shell.returnvalue;

import com.rawcod.jerminal.shell.entry.ShellAutoComplete;

/**
 * User: yevgenyk
 * Date: 08/01/14
 */
public class ShellAutoCompleteReturnValue {
    private final boolean success;
    private final ShellAutoComplete autoComplete;
    private final boolean trailingSpace;
    private final ShellErrorCode errorCode;
    private final String errorMessage;
    private String usage;

    protected ShellAutoCompleteReturnValue(boolean success,
                                           ShellAutoComplete autoComplete,
                                           boolean trailingSpace,
                                           ShellErrorCode errorCode,
                                           String errorMessage) {
        this.success = success;
        this.autoComplete = autoComplete;
        this.trailingSpace = trailingSpace;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public ShellAutoComplete getAutoComplete() {
        return autoComplete;
    }

    public boolean isTrailingSpace() {
        return trailingSpace;
    }

    public ShellErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getUsage() {
        return usage;
    }

    public ShellAutoCompleteReturnValue withUsage(String usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShellAutoCompleteReturnValue{");
        sb.append("success=").append(success);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", autoComplete=").append(autoComplete);
        sb.append('}');
        return sb.toString();
    }

    public static ShellAutoCompleteReturnValue success(ShellAutoComplete autoComplete) {
        return new ShellAutoCompleteReturnValue(true, autoComplete, true, null, null);
    }

    public static ShellAutoCompleteReturnValue successNoTrailingSpace(ShellAutoComplete autoComplete) {
        return new ShellAutoCompleteReturnValue(true, autoComplete, false, null, null);
    }

    public static ShellAutoCompleteReturnValue failure(ShellErrorCode errorCode,
                                                       String message,
                                                       ShellAutoComplete autoComplete) {
        return new ShellAutoCompleteReturnValue(false, autoComplete, false, errorCode, message);
    }

    public static ShellAutoCompleteReturnValue failure(ShellErrorCode errorCode, String message) {
        return failure(errorCode, message, ShellAutoComplete.none());
    }

    public static ShellAutoCompleteReturnValue failureInvalidCommand(String errorMessage, ShellAutoComplete autoComplete) {
        return failure(ShellErrorCode.INVALID_COMMAND, errorMessage, autoComplete);
    }

    public static ShellAutoCompleteReturnValue failureInvalidArgument(String errorMessage) {
        return failure(ShellErrorCode.INVALID_ARGUMENT, errorMessage);
    }

    public static ShellAutoCompleteReturnValue failureInvalidArgument(String errorMessage,
                                                                      ShellAutoComplete autoComplete) {
        return failure(ShellErrorCode.INVALID_ARGUMENT, errorMessage, autoComplete);
    }

    public static ShellAutoCompleteReturnValue failureMissingArgument(String argName) {
        final String errorMessage = String.format("Argument not provided: '%s'", argName);
        return failure(ShellErrorCode.MISSING_ARGUMENT, errorMessage);
    }

    public static ShellAutoCompleteReturnValue failureExcessArgument(Object excessArg) {
        final String errorMessage = String.format("Excess arguments provided: %s", excessArg);
        return failure(ShellErrorCode.EXCESS_ARGUMENT, errorMessage);
    }

    public static ShellAutoCompleteReturnValue failureInternalError(String errorMessage) {
        return failure(ShellErrorCode.INTERNAL_ERROR, errorMessage);
    }

    public static ShellAutoCompleteReturnValue from(ShellParseReturnValue<?> returnValue) {
        return new ShellAutoCompleteReturnValue(returnValue.isSuccess(),
                                                returnValue.getAutoComplete(),
                                                false,
                                                returnValue.getErrorCode(),
                                                returnValue.getErrorMessage());
    }
}

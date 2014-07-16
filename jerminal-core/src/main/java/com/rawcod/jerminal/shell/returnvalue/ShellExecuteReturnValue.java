package com.rawcod.jerminal.shell.returnvalue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class ShellExecuteReturnValue {
    private final boolean success;
    private final String message;

    public ShellExecuteReturnValue(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShellExecuteReturnValue{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static ShellExecuteReturnValue successNoMessage() {
        return new ShellExecuteReturnValue(true, null);
    }

    public static ShellExecuteReturnValue success(String message) {
        return new ShellExecuteReturnValue(true, message);
    }

    public static ShellExecuteReturnValue success(String format, Object arg) {
        final String message = String.format(format, arg);
        return new ShellExecuteReturnValue(true, message);
    }

    public static ShellExecuteReturnValue success(String format, Object arg1, Object arg2) {
        final String message = String.format(format, arg1, arg2);
        return new ShellExecuteReturnValue(true, message);
    }

    public static ShellExecuteReturnValue success(String format, Object arg1, Object arg2, Object arg3) {
        final String message = String.format(format, arg1, arg2, arg3);
        return new ShellExecuteReturnValue(true, message);
    }

    public static ShellExecuteReturnValue failure(String message) {
        return new ShellExecuteReturnValue(false, message);
    }

    public static ShellExecuteReturnValue failure(String format, Object arg) {
        final String message = String.format(format, arg);
        return new ShellExecuteReturnValue(false, message);
    }

    public static ShellExecuteReturnValue failure(String format, Object arg1, Object arg2) {
        final String message = String.format(format, arg1, arg2);
        return new ShellExecuteReturnValue(false, message);
    }

    public static ShellExecuteReturnValue failure(String format, Object arg1, Object arg2, Object arg3) {
        final String message = String.format(format, arg1, arg2, arg3);
        return new ShellExecuteReturnValue(false, message);
    }
}

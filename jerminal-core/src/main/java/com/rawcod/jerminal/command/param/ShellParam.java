package com.rawcod.jerminal.command.param;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public interface ShellParam {
    String getName();
    String getDescription();

    ShellParamParser getParser();

    // FIXME: Not amazing... leaky abstraction.
    boolean isOptional();
    Object getDefaultValue();
}

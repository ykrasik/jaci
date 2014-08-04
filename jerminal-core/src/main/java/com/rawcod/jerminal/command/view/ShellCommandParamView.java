package com.rawcod.jerminal.command.view;

import com.rawcod.jerminal.command.parameters.ParamType;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 20:49
 */
public interface ShellCommandParamView {
    String getName();
    String getDescription();
    ParamType getType();

    String getExternalForm();
}

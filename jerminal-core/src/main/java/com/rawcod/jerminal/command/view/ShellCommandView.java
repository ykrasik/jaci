package com.rawcod.jerminal.command.view;

import java.util.List;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 20:48
 */
public interface ShellCommandView {
    String getName();
    String getDescription();

    List<ShellCommandParamView> getParams();
}

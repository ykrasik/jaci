package com.rawcod.jerminal.command.parameters;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 22:28
 */
public interface OptionalCommandParam extends CommandParam {
    Object getDefaultValue();
}

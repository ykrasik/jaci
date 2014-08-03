package com.rawcod.jerminal.command.parameters;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 04/08/2014
 * Time: 01:37
 */
public class RestrictedCommandParam implements CommandParam {
    private final CommandParam delegate;

    public RestrictedCommandParam(CommandParam delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public ParamType getType() {
        return delegate.getType();
    }

    @Override
    public ParseParamValueReturnValue parse(Optional<String> rawValue, ParamParseContext context) {
        throw restrictedException();
    }

    @Override
    public AutoCompleteReturnValue autoComplete(Optional<String> prefix, ParamParseContext context) {
        throw restrictedException();
    }

    private UnsupportedOperationException restrictedException() {
        final String message = String.format("Operation not allowed on restricted command '%s'!", getName());
        throw new UnsupportedOperationException(message);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}

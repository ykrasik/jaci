package com.rawcod.jerminal.command.parameters.optional;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.ParamType;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 21:02
 */
public class OptionalParam<T> implements CommandParam {
    private final CommandParam delegate;
    private final Supplier<T> defaultValueSupplier;

    public OptionalParam(CommandParam delegate, Supplier<T> defaultValueSupplier) {
        this.delegate = delegate;
        this.defaultValueSupplier = defaultValueSupplier;
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
        return ParamType.OPTIONAL;
    }

    @Override
    public String getExternalForm() {
        // Replace the original param's braces with '[]'.
        final String originalExternalForm = delegate.getExternalForm();
        return '[' + originalExternalForm.substring(1, originalExternalForm.length() - 1) + ']';
    }

    @Override
    public Object parse(String rawValue, ParseParamContext context) throws ParseException {
        return delegate.parse(rawValue, context);
    }

    @Override
    public Object unbound(ParseParamContext context) throws ParseException {
        return defaultValueSupplier.get();
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) throws ParseException {
        return delegate.autoComplete(prefix, context);
    }

    @Override
    public String toString() {
        return getExternalForm();
    }
}

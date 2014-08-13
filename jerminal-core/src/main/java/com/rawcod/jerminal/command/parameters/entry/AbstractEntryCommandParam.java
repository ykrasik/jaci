package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 20:42
 */
public abstract class AbstractEntryCommandParam<T> extends AbstractMandatoryCommandParam<T> {
    protected AbstractEntryCommandParam(String name, String description) {
        super(name, description);
    }

    @Override
    public T parse(String rawValue, ParseParamContext context) throws ParseException {
        final ShellFileSystem fileSystem = context.getFileSystem();
        return doParse(rawValue, fileSystem);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) throws ParseException {
        final ShellFileSystem fileSystem = context.getFileSystem();
        return doAutoComplete(prefix, fileSystem);
    }

    protected abstract T doParse(String rawValue, ShellFileSystem fileSystem) throws ParseException;
    protected abstract AutoCompleteReturnValue doAutoComplete(String prefix, ShellFileSystem fileSystem) throws ParseException;
}

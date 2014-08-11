package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParseEntryReturnValue.ParseEntryReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 20:42
 */
public abstract class AbstractEntryCommandParam extends AbstractMandatoryCommandParam {
    protected AbstractEntryCommandParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        final ShellFileSystem fileSystem = context.getFileSystem();
        final ParseEntryReturnValue returnValue = doParse(rawValue, fileSystem);
        if (returnValue.isFailure()) {
            return ParseParamValueReturnValue.failure(returnValue.getFailure());
        }

        final ParseEntryReturnValueSuccess success = returnValue.getSuccess();
        return ParseParamValueReturnValue.success(success.getEntry());
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) {
        final ShellFileSystem fileSystem = context.getFileSystem();
        return doAutoComplete(prefix, fileSystem);
    }

    protected abstract ParseEntryReturnValue doParse(String rawValue, ShellFileSystem fileSystem);
    protected abstract AutoCompleteReturnValue doAutoComplete(String prefix, ShellFileSystem fileSystem);
}

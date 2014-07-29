package com.rawcod.jerminal.command.parameters.entry;

import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue.ParsePathReturnValueSuccess;
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
    protected ParseParamValueReturnValue parse(String rawValue, ParamParseContext context) {
        final FileSystemManager fileSystemManager = context.getFileSystemManager();
        final ParsePathReturnValue returnValue = doParse(rawValue, fileSystemManager);
        if (returnValue.isFailure()) {
            return ParseParamValueReturnValue.failure(returnValue.getFailure());
        }

        final ParsePathReturnValueSuccess success = returnValue.getSuccess();
        return ParseParamValueReturnValue.success(success.getLastEntry());
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParamParseContext context) {
        final FileSystemManager fileSystemManager = context.getFileSystemManager();
        return doAutoComplete(prefix, fileSystemManager);
    }

    protected abstract ParsePathReturnValue doParse(String rawValue, FileSystemManager fileSystemManager);
    protected abstract AutoCompleteReturnValue doAutoComplete(String prefix, FileSystemManager fileSystemManager);
}

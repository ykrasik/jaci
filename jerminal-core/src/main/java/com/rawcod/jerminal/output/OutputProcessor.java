package com.rawcod.jerminal.output;

import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValueSuccess;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueSuccess;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 11:58
 */
public interface OutputProcessor {
    void clearCommandLine();
    void setCommandLine(String commandLine);

    void processAutoCompleteSuccess(AutoCompleteReturnValueSuccess returnValue);
    void processAutoCompleteFailure(AutoCompleteReturnValueFailure returnValue);

    void processExecuteOutputSuccess(ExecuteReturnValueSuccess returnValue);
    void processExecuteOutputFailure(ExecuteReturnValueFailure returnValue);
}

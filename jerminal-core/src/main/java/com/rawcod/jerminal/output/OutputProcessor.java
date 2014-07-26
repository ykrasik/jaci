package com.rawcod.jerminal.output;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValueFailure;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 11:58
 */
public interface OutputProcessor {
    void clearCommandLine();
    void setCommandLine(String commandLine);

    void println(String message);

    void displayAutoCompleteSuggestions(List<String> suggestions);
    void processAutoCompleteFailure(AutoCompleteReturnValueFailure returnValue);

    void processExecuteOutputSuccess(String output, Optional<Object> returnValue);
    void processExecuteOutputFailure(ExecuteReturnValueFailure returnValue);
}

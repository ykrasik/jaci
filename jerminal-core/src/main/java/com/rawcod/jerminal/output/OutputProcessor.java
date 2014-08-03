package com.rawcod.jerminal.output;

import com.google.common.base.Optional;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.parse.ParseReturnValueFailure;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 11:58
 */
public interface OutputProcessor {
    void clearCommandLine();
    void setCommandLine(String commandLine);

    void handleParseFailure(ParseReturnValueFailure failure);

    void displayAutoCompleteSuggestions(List<String> suggestions);
    void handleAutoCompleteFailure(AutoCompleteReturnValueFailure failure);

    void handleExecuteCommandSuccess(String output, Optional<Object> returnValue);
    void handleExecuteCommandFailure(ExecuteReturnValueFailure failure);
}

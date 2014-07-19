package com.rawcod.jerminal.returnvalue;

import com.rawcod.jerminal.returnvalue.ReturnValue.Failure;
import com.rawcod.jerminal.returnvalue.ReturnValue.Success;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 16:52
 */
public interface ReturnValue<S extends Success, F extends Failure> extends Failable {
    boolean isFailure();

    S getSuccess();
    F getFailure();

    interface Success extends Failable {
    }

    interface Failure extends Failable {
    }
}

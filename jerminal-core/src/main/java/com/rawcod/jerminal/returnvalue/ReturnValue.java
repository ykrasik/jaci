package com.rawcod.jerminal.returnvalue;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 16:52
 */
public interface ReturnValue<S extends Success, F extends Failure> extends Failable {
    boolean isFailure();

    S getSuccess();
    F getFailure();

}

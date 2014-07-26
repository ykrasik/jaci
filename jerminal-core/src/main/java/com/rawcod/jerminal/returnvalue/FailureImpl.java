package com.rawcod.jerminal.returnvalue;

/**
* User: ykrasik
* Date: 26/07/2014
* Time: 22:36
*/
public class FailureImpl implements Failure {
    @Override
    public boolean isSuccess() {
        return false;
    }
}

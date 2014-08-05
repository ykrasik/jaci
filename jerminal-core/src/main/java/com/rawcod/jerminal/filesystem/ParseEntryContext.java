package com.rawcod.jerminal.filesystem;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 23:11
 */
public class ParseEntryContext {
    private final GlobalCommandManager globalCommandManager;

    public ParseEntryContext(GlobalCommandManager globalCommandManager) {
        this.globalCommandManager = globalCommandManager;
    }

    public GlobalCommandManager getGlobalCommandManager() {
        return globalCommandManager;
    }
}

package com.rawcod.jerminal.filesystem;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 23:11
 */
public class ParseEntryContext {
    private final GlobalCommandRepository globalCommandRepository;

    public ParseEntryContext(GlobalCommandRepository globalCommandRepository) {
        this.globalCommandRepository = globalCommandRepository;
    }

    public GlobalCommandRepository getGlobalCommandRepository() {
        return globalCommandRepository;
    }
}

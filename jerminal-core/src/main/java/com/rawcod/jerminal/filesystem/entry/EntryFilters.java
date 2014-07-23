package com.rawcod.jerminal.filesystem.entry;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 23:37
 */
@SuppressWarnings("unchecked")
public final class EntryFilters {
    private EntryFilters() {

    }

    public static final Predicate<ShellEntry> NO_FILTER = Predicates.alwaysTrue();

    public static final Predicate<ShellEntry> FILE_FILTER = new Predicate<ShellEntry>() {
        @Override
        public boolean apply(ShellEntry value) {
            return !value.isDirectory();
        }
    };

    public static final Predicate<ShellEntry> DIRECTORY_FILTER = new Predicate<ShellEntry>() {
        @Override
        public boolean apply(ShellEntry value) {
            return value.isDirectory();
        }
    };
}

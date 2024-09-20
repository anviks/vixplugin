package me.captainpotatoaim.myplugin.util;

import java.util.Arrays;
import java.util.Collection;

public class CollectionsHelper {
    public static <T> Collection<? extends T> allExcept(Collection<? extends T> collection, T except) {
        return collection.stream()
                .filter(element -> !element.equals(except))
                .toList();
    }
}

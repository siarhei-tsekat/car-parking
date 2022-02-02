package net.tsekot.util;

import java.util.stream.Stream;

public class ObjectUtils {
    public static boolean notBlank(String... elems) {
        return Stream.of(elems).noneMatch(elem -> elem == null || elem.isBlank());
    }
}

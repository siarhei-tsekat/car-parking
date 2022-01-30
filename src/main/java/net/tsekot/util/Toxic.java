package net.tsekot.util;

import java.util.function.Function;

@FunctionalInterface
public interface Toxic<IN, OUT> {
    OUT apply(IN param) throws Exception;

    static <IN, OUT> Function<IN, OUT> Try(Toxic<IN, OUT> function) {
        return params -> {
            try {
                return function.apply(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

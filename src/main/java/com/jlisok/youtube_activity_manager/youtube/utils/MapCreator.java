package com.jlisok.youtube_activity_manager.youtube.utils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapCreator {

    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, Function<T, K> toKey, Function<T, V> toValue) {
        return collection
                .stream()
                .collect(Collectors.toMap(toKey, toValue));
    }
}

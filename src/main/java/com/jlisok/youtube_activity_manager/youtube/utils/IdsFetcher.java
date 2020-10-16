package com.jlisok.youtube_activity_manager.youtube.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IdsFetcher {

    public static <T, S> List<S> getIdsFrom(Collection<T> collection, Function<T, S> toIds) {
        return collection.stream()
                         .map(toIds)
                         .distinct()
                         .collect(Collectors.toList());
    }
}

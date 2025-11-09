package com.zerox80.riotapi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class ListUtils {

    private ListUtils() {
        // Private constructor to prevent instantiation
    }

    
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive.");
        }

        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(new ArrayList<>(
                    list.subList(i, Math.min(i + size, list.size()))
            ));
        }
        return partitions;
    }
}

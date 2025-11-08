package com.zerox80.riotapi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility helper methods for working with {@link java.util.List} collections.
 *
 * <p>The methods encapsulate reusable list transformations (for example, partitioning)
 * in a central location so that services can remain focused on domain logic. All utilities
 * are static and null-safe, making the class an easy drop-in dependency across modules.</p>
 */
public final class ListUtils {

    private ListUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Partitions a list into successive sublists of a specified size.
     *
     * @param list The list to be partitioned.
     * @param size The desired size of each sublist (the last sublist may be smaller).
     * @param <T>  The type of elements in the list.
     * @return A list of successive sublists of the original list.
     * @throws IllegalArgumentException if the list is null or the size is not positive.
     */
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

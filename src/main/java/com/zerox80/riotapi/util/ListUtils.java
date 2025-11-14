// Package declaration: Defines that this class belongs to the util (utility) package
package com.zerox80.riotapi.util;

// Import for ArrayList to create partitioned sublists
import java.util.ArrayList;
// Import for Collections utility methods
import java.util.Collections;
// Import for Java List interface
import java.util.List;


/**
 * Utility class providing helper methods for List operations.
 * Contains static utility methods for manipulating and partitioning lists.
 *
 * This class cannot be instantiated (private constructor) and serves solely
 * as a container for static utility methods. Currently provides functionality
 * to partition large lists into smaller sublists of a specified size.
 */
public final class ListUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an exception if someone tries to instantiate via reflection.
     */
    private ListUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Partitions a list into smaller sublists of a specified size.
     * Useful for batch processing large lists or making chunked API requests.
     *
     * For example, partitioning [1,2,3,4,5,6,7] with size 3 yields:
     * [[1,2,3], [4,5,6], [7]]
     *
     * @param <T> The type of elements in the list
     * @param list The list to partition (must not be null)
     * @param size The maximum size of each partition (must be positive)
     * @return A list of sublists, each containing at most 'size' elements.
     *         Returns an empty list if the input list is empty.
     * @throws IllegalArgumentException if list is null or size is not positive
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

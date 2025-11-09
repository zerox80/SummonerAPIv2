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
     * <p>This method divides a list into consecutive chunks of the specified size.
     * The final sublist may be smaller than the requested size if the original
     * list size is not evenly divisible by the partition size.</p>
     * 
     * <p><strong>Performance Characteristics:</strong></p>
     * <ul>
     *   <li>Time complexity: O(n) where n is the list size</li>
     *   <li>Space complexity: O(n) for the returned partition structure</li>
     *   <li>Memory usage: Creates new ArrayList objects for each partition</li>
     * </ul>
     * 
     * <p><strong>Edge Cases:</strong></p>
     * <ul>
     *   <li>Empty list returns empty list (no partitions created)</li>
     *   <li>Single element list returns one partition</li>
     *   <li>Size larger than list returns single partition with all elements</li>
     *   <li>Size equals 1 returns individual element partitions</li>
     * </ul>
     * 
     * <p><strong>Common Use Cases:</strong></p>
     * <ul>
     *   <li>Batch processing large datasets (e.g., API calls in chunks)</li>
     *   <li>Parallel processing of data subsets</li>
     *   <li>Database batch operations</li>
     *   <li>UI pagination with fixed page sizes</li>
     * </ul>
     * 
     * @param list The list to be partitioned (must not be null)
     * @param size The desired size of each sublist (must be positive, last sublist may be smaller)
     * @param <T> The type of elements in the list (any non-null type)
     * @return A list of successive sublists of the original list, preserving original order
     * @throws IllegalArgumentException if the list is null or the size is not positive
     * 
     * @example
     * // Basic partitioning
     * List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
     * List<List<Integer>> partitions = ListUtils.partition(numbers, 3);
     * // Result: [[1, 2, 3], [4, 5, 6], [7]]
     * 
     * @example
     * // Processing API calls in batches
     * List<String> ids = getLargeListOfIds();
     * List<List<String>> batches = ListUtils.partition(ids, 10); // 10 per API call
     * for (List<String> batch : batches) {
     *     processBatch(batch); // Avoid rate limits
     * }
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

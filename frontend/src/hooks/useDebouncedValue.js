/**
 * React hook for debouncing values with customizable delay.
 * 
 * <p>This hook provides debouncing functionality to delay the update of a value
 * until after a specified amount of time has passed without the value changing.
 * It's commonly used for search inputs, form validation, and other scenarios
 * where you want to wait for user input to settle before taking action.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Configurable delay time (default: 200ms)</li>
 *   <li>Automatic cleanup of pending timeouts</li>
 *   <li>Immediate update on initial render</li>
 *   <li>Works with any value type</li>
 * </ul>
 * 
 * @module hooks/useDebouncedValue
 * @author zerox80
 * @version 2.0
 */

import { useEffect, useState } from 'react';

/**
 * Custom hook that returns a debounced version of the provided value.
 * 
 * <p>This hook delays updating the returned value until after the specified delay
 * has passed since the last value change. This is useful for implementing search
 * functionality, form validation, or any scenario where you want to wait for
 * rapid changes to settle before processing.</p>
 * 
 * <p>The hook automatically handles cleanup of pending timeouts when the component
 * unmounts or when the delay changes, preventing memory leaks and stale updates.</p>
 * 
 * @param {*} value - The value to debounce (can be any type)
 * @param {number} [delay=200] - The delay in milliseconds to wait before updating the debounced value
 * @returns {*} The debounced value that updates after the specified delay
 * 
 * @example
 * // Basic usage for search input
 * const [searchTerm, setSearchTerm] = useState('');
 * const debouncedSearch = useDebouncedValue(searchTerm, 300);
 * 
 * // Use debouncedSearch for API calls to avoid excessive requests
 * useEffect(() => {
 *   if (debouncedSearch) {
 *     searchApi(debouncedSearch);
 *   }
 * }, [debouncedSearch]);
 * 
 * @example
 * // Custom delay for form validation
 * const [email, setEmail] = useState('');
 * const debouncedEmail = useDebouncedValue(email, 500);
 * 
 * @example
 * // Immediate update with zero delay
 * const immediateValue = useDebouncedValue(value, 0);
 */
export default function useDebouncedValue(value, delay = 200) {
  const [debounced, setDebounced] = useState(value);

  useEffect(() => {
    const handle = setTimeout(() => {
      setDebounced(value);
    }, delay);
    return () => clearTimeout(handle);
  }, [value, delay]);

  return debounced;
}

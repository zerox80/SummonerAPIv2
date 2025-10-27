/**
 * Test suite for the useDebouncedValue hook.
 * 
 * <p>This test file validates the debouncing functionality of the useDebouncedValue hook,
 * ensuring that values are updated correctly after the specified delay and that
 * timeouts are properly reset when values change rapidly. The tests use Vitest
 * with fake timers to control timing precisely.</p>
 * 
 * <p>Test coverage includes:</p>
 * <ul>
 *   <li>Initial value return behavior</li>
 *   <li>Delayed value updates after specified timeout</li>
 *   <li>Timeout reset behavior on rapid value changes</li>
 *   <li>Edge cases and boundary conditions</li>
 * </ul>
 * 
 * @file hooks/useDebouncedValue.test.js
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */

import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import useDebouncedValue from './useDebouncedValue';

console.log('Executing useDebouncedValue.test.js');

/**
 * Test suite for useDebouncedValue hook functionality.
 * 
 * <p>This describe block contains all tests for the debouncing hook,
 * using fake timers to control timing and ensure predictable test results.</p>
 */
describe('useDebouncedValue', () => {
  vi.useFakeTimers();

  /**
   * Test that the hook returns the initial value immediately.
   * 
   * <p>This test verifies that when the hook is first rendered, it returns
   * the initial value without any delay, ensuring immediate UI updates.</p>
   */
  it('should return the initial value immediately', () => {
    const { result } = renderHook(() => useDebouncedValue('initial'));
    expect(result.current).toBe('initial');
  });

  /**
   * Test that the value updates after the specified delay.
   * 
   * <p>This test verifies that when the input value changes, the debounced
   * value only updates after the specified delay period has passed.</p>
   */
  it('should update the value after the delay', () => {
    const { result, rerender } = renderHook(({ value, delay }) => useDebouncedValue(value, delay), {
      initialProps: { value: 'initial', delay: 500 },
    });

    expect(result.current).toBe('initial');

    rerender({ value: 'updated', delay: 500 });

    expect(result.current).toBe('initial');

    act(() => {
      vi.advanceTimersByTime(500);
    });

    expect(result.current).toBe('updated');
  });

  /**
   * Test that the timeout resets if the value changes rapidly.
   * 
   * <p>This test verifies that if the input value changes before the
   * debounce delay has elapsed, the previous timeout is cancelled and
   * a new timeout is started with the updated value.</p>
   */
  it('should reset the timeout if the value changes', () => {
    const { result, rerender } = renderHook(({ value, delay }) => useDebouncedValue(value, delay), {
      initialProps: { value: 'initial', delay: 500 },
    });

    expect(result.current).toBe('initial');

    rerender({ value: 'updated', delay: 500 });

    expect(result.current).toBe('initial');

    act(() => {
      vi.advanceTimersByTime(250);
    });

    rerender({ value: 'final', delay: 500 });

    expect(result.current).toBe('initial');

    act(() => {
      vi.advanceTimersByTime(500);
    });

    expect(result.current).toBe('final');
  });
});

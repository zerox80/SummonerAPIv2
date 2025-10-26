import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import useDebouncedValue from './useDebouncedValue';

console.log('Executing useDebouncedValue.test.js');

describe('useDebouncedValue', () => {
  vi.useFakeTimers();

  it('should return the initial value immediately', () => {
    const { result } = renderHook(() => useDebouncedValue('initial'));
    expect(result.current).toBe('initial');
  });

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

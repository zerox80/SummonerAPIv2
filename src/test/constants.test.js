import { describe, it, expect, vi, beforeEach } from 'vitest'
import { DEBOUNCE_DELAYS, QUEUE_TYPES, STORAGE_KEYS } from '../main/resources/static/js/constants.js'

describe('Constants', () => {
  it('should have correct debounce delays', () => {
    expect(DEBOUNCE_DELAYS.SEARCH).toBe(300)
    expect(DEBOUNCE_DELAYS.FILTER).toBe(200)
  })

  it('should have correct queue types', () => {
    expect(QUEUE_TYPES.RANKED_SOLO).toBe(420)
    expect(QUEUE_TYPES.RANKED_FLEX).toBe(440)
  })

  it('should have correct storage keys', () => {
    expect(STORAGE_KEYS.SEARCH_HISTORY).toBe('searchHistory')
    expect(STORAGE_KEYS.THEME).toBe('theme')
  })
})

describe('Queue Type Validation', () => {
  it('should identify ranked queues correctly', () => {
    const isRankedQ = (q) => q === QUEUE_TYPES.RANKED_SOLO ||
                              q === QUEUE_TYPES.RANKED_FLEX ||
                              q === QUEUE_TYPES.TFT_RANKED

    expect(isRankedQ(QUEUE_TYPES.RANKED_SOLO)).toBe(true)
    expect(isRankedQ(QUEUE_TYPES.NORMAL_DRAFT)).toBe(false)
    expect(isRankedQ(999)).toBe(false)
  })
})

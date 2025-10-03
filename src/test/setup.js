// Test setup for Vitest
import { beforeAll } from 'vitest'

// Mock Chart.js globally
global.Chart = class Chart {
  constructor() {
    this.destroy = vi.fn()
    this.update = vi.fn()
    this.resize = vi.fn()
  }
}

// Setup global mocks
beforeAll(() => {
  // Mock localStorage
  Object.defineProperty(window, 'localStorage', {
    value: {
      getItem: vi.fn(() => null),
      setItem: vi.fn(() => null),
      removeItem: vi.fn(() => null),
      clear: vi.fn(() => null)
    },
    writable: true
  })

  // Mock AbortController
  global.AbortController = vi.fn(() => ({
    abort: vi.fn(),
    signal: { aborted: false }
  }))
})

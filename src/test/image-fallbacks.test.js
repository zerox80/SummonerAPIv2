import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { initImageFallbacks } from '../main/resources/static/js/image-fallbacks.js'

describe('Image Fallbacks', () => {
  let mockDocument

  beforeEach(() => {
    // Mock document
    mockDocument = {
      body: {
        hasAttribute: vi.fn().mockReturnValue(false),
        setAttribute: vi.fn(),
        querySelectorAll: vi.fn()
      },
      addEventListener: vi.fn(),
      readyState: 'loading'
    }

    global.document = mockDocument
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('should set initialization flag on body', () => {
    const mockImg = {
      __fallbackBound: false,
      addEventListener: vi.fn()
    }

    mockDocument.querySelectorAll.mockReturnValue([mockImg])

    initImageFallbacks()

    expect(mockDocument.body.setAttribute).toHaveBeenCalledWith('data-img-fallback-installed', 'true')
  })

  it('should not initialize if already done', () => {
    mockDocument.body.hasAttribute.mockReturnValue(true)

    initImageFallbacks()

    expect(mockDocument.body.setAttribute).not.toHaveBeenCalled()
  })

  it('should attach error handlers to images', () => {
    const mockImg = {
      __fallbackBound: false,
      addEventListener: vi.fn(),
      style: {}
    }

    mockDocument.querySelectorAll.mockReturnValue([mockImg])

    initImageFallbacks()

    expect(mockImg.addEventListener).toHaveBeenCalledWith('error', expect.any(Function), { once: false })
    expect(mockImg.__fallbackBound).toBe(true)
  })

  it('should not attach handlers to already bound images', () => {
    const mockImg = {
      __fallbackBound: true,
      addEventListener: vi.fn()
    }

    mockDocument.querySelectorAll.mockReturnValue([mockImg])

    initImageFallbacks()

    expect(mockImg.addEventListener).not.toHaveBeenCalled()
  })
})

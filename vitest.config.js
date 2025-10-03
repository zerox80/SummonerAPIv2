/// <reference types="vitest" />
import { defineConfig } from 'vite'
import { resolve } from 'path'

export default defineConfig({
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.js']
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src/main/resources/static/js')
    }
  }
})

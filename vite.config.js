import { defineConfig } from 'vite'
import { resolve } from 'path'
import react from '@vitejs/plugin-react'

const isProduction = process.env.NODE_ENV === 'production'

export default defineConfig({
  root: 'frontend',
  plugins: [react()],
  envDir: '../',
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
    sourcemap: !isProduction,
    minify: isProduction ? 'terser' : false,
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'frontend/index.html')
      },
      output: {
        entryFileNames: 'assets/[name]-[hash].js',
        chunkFileNames: 'assets/[name]-[hash].js',
        assetFileNames: ({ name }) => {
          if (name && name.endsWith('.css')) {
            return 'assets/[name]-[hash][extname]'
          }
          return 'assets/[name]-[hash][extname]'
        }
      }
    },
    terserOptions: {
      compress: {
        drop_console: isProduction,
        drop_debugger: isProduction
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'frontend/src')
    }
  }
})

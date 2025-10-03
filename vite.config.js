import { defineConfig } from 'vite'
import { resolve } from 'path'

export default defineConfig({
  root: 'src/main/resources/static',
  build: {
    outDir: '../../../../target/classes/static',
    emptyOutDir: true,
    rollupOptions: {
      input: {
        'js/app': resolve(__dirname, 'src/main/resources/static/js/app.js'),
        'js/main': resolve(__dirname, 'src/main/resources/static/js/main.js'),
        'js/champions': resolve(__dirname, 'src/main/resources/static/js/champions.js')
      },
      output: {
        entryFileNames: 'js/[name].js',
        chunkFileNames: 'js/[name]-[hash].js',
        assetFileNames: 'css/[name]-[hash][extname]'
      }
    },
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    sourcemap: process.env.NODE_ENV === 'development'
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
      '@': resolve(__dirname, 'src/main/resources/static/js')
    }
  }
})

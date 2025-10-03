import { defineConfig } from 'vite'
import { resolve } from 'path'

export default defineConfig({
  root: 'src/main/resources/static',
  build: {
    outDir: '../../../../src/main/resources/static',
    emptyOutDir: false, // Don't empty, preserve other static files
    rollupOptions: {
      input: {
        'js/app': resolve(__dirname, 'src/main/resources/static/js/app.js'),
        'js/main': resolve(__dirname, 'src/main/resources/static/js/main.js'),
        'js/champions': resolve(__dirname, 'src/main/resources/static/js/champions.js')
      },
      output: {
        entryFileNames: 'js/[name].js',
        chunkFileNames: 'js/[name]-[hash].js',
        assetFileNames: (assetInfo) => {
          if (assetInfo.name?.endsWith('.css')) {
            return 'css/[name][extname]';
          }
          return 'assets/[name]-[hash][extname]';
        }
      }
    },
    minify: process.env.NODE_ENV === 'production' ? 'terser' : false,
    terserOptions: {
      compress: {
        drop_console: process.env.NODE_ENV === 'production',
        drop_debugger: process.env.NODE_ENV === 'production'
      }
    },
    sourcemap: process.env.NODE_ENV !== 'production'
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

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
    server: {
      proxy: {
          // 프론트에서 /api 로 호출하면 8080 백엔드로 프록시됨
          '/api': { target: 'http://localhost:8080', changeOrigin: true }
      }
    }
})

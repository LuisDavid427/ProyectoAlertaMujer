import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // 👈 Añade esto para que exponga el servidor a tu red local
    port: 5173
  }
})
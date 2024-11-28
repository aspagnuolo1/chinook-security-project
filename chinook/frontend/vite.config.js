import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      external: ['axios'],  // Se vuoi che axios venga esternalizzato
    },
  },
  server: {
    host: '0.0.0.0',
    port: 5173, // opzionale, specifica il porto se necessario
  },
})

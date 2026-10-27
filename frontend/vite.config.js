import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const BACKEND_ORIGIN = 'http://localhost:8080';
const BACKEND_CONTEXT_PATH = '/seller-rating-system';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: BACKEND_ORIGIN,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, BACKEND_CONTEXT_PATH),
      },
    },
  },
});

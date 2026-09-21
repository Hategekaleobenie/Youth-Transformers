import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  // Firebase Hosting serves the app from the domain root.
  // GitHub Pages previously needed /Youth-Transformers/, but that breaks
  // asset URLs on Firebase Hosting and results in a blank page.
  base: '/',
  plugins: [react()],
  server: {
    port: 3000,
    host: true
  },
  build: {
    outDir: 'dist',
    sourcemap: false
  }
});

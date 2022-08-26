import { defineConfig } from "vite";
import preact from "@preact/preset-vite";
import join from "path";

// https://vitejs.dev/config/
export default defineConfig({
  base: "./",
  build: {
    outDir: "../app/src/main/assets/preferences-ui",
  },
  resolve: {
    alias: {
      "@": join.join(__dirname, "./src"),
      "@assets": join.join(__dirname, "./src/assets"),
    },
  },
  plugins: [preact()],
  esbuild: {},
});

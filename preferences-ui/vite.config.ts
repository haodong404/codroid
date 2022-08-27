import { defineConfig } from "vite";
import preact from "@preact/preset-vite";
import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
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
  plugins: [
    preact(),
    createSvgIconsPlugin({
      iconDirs: [join.join(__dirname, "./src/assets/icons")],
      symbolId: "icon-[dir]-[name]",
    }),
  ],
  esbuild: {},
});

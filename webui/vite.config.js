import {defineConfig} from "vite";
import path from "path";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            "/api": "http://localhost:7867"
        }
    },
    resolve: {
        alias: {
            '@contexts': path.resolve(__dirname, './src/common/contexts'),
            '@components': path.resolve(__dirname, './src/common/components'),
            '@': path.resolve(__dirname, './src'),
        }
    }
})

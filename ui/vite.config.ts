import path from "path"
import react from "@vitejs/plugin-react"
import {defineConfig} from "vite"

export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            "/api": "http://localhost:7867"
        }
    },
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src")
        },
    },
})

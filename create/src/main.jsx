import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import theme from "./common/themes/default.js";

import '@fontsource/inter/300.css';
import '@fontsource/inter/400.css';
import '@fontsource/inter/500.css';
import '@fontsource/inter/700.css';
import {CssBaseline, ThemeProvider} from "@mui/material";

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <App/>
        </ThemeProvider>
    </React.StrictMode>,
);
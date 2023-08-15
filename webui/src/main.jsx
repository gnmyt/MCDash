import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import {SettingsProvider} from "@contexts/Settings";


ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <SettingsProvider>
          <App />
      </SettingsProvider>
  </React.StrictMode>,
)

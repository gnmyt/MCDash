import {CssBaseline, ThemeProvider,} from "@mui/material";
import darkTheme from "@/common/themes/dark.js";
import lightTheme from "@/common/themes/light.js";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "@/states/Login";
import Root from "@/states/Root";
import {routes} from "@/common/routes/server.jsx";
import {TokenProvider} from "@contexts/Token";
import {PlayerProvider} from "@contexts/Players";
import {useContext, useState} from "react";
import {SettingsContext} from "@contexts/Settings";
import i18n from "./i18n.js";

const App = () => {

    const {theme} = useContext(SettingsContext);
    const [translationsLoaded, setTranslationsLoaded] = useState(false);

    const router = createBrowserRouter([
        {path: "/login", element: <Login />},
        {path: "/", element: <Root />, children: routes}
    ]);

    i18n.on("initialized", () => setTranslationsLoaded(true));

    if (!translationsLoaded) return <></>;

    return (
        <ThemeProvider theme={theme === "dark" ? darkTheme : lightTheme}>
            <CssBaseline/>
            <TokenProvider>
                <PlayerProvider>
                    <RouterProvider router={router}/>
                </PlayerProvider>
            </TokenProvider>
        </ThemeProvider>
    )
}

export default App;
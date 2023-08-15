import {CssBaseline, ThemeProvider,} from "@mui/material";
import darkTheme from "@/common/themes/dark.js";
import lightTheme from "@/common/themes/light.js";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "@/states/Login";
import Root from "@/states/Root";
import {routes} from "@/common/routes/server.jsx";
import {TokenProvider} from "@contexts/Token";
import {PlayerProvider} from "@contexts/Players";
import {useContext} from "react";
import {SettingsContext} from "@contexts/Settings";

const App = () => {

    const {theme} = useContext(SettingsContext);

    const router = createBrowserRouter([
        {path: "/login", element: <Login />},
        {path: "/", element: <Root />, children: routes}
    ]);

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
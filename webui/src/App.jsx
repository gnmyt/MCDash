import {CssBaseline, ThemeProvider,} from "@mui/material";
import theme from "@/common/themes/dark.js";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "@/states/Login";
import Root from "@/states/Root";
import routes from "@/common/routes/server.jsx";
import {TokenProvider} from "@contexts/Token";

const App = () => {
    const router = createBrowserRouter([
        {path: "/login", element: <Login />},
        {path: "/", element: <Root />, children: routes}
    ]);

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline/>
            <TokenProvider>
                <RouterProvider router={router}/>
            </TokenProvider>
        </ThemeProvider>
    )
}

export default App;
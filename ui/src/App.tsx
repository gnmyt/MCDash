import Login from "@/states/Login/Login.tsx";

import i18n from "./i18n.ts";
import {useState} from "react";
import {createBrowserRouter, RouterProvider} from "react-router-dom";

import {getRoutes} from "@/states/Root/routes.tsx";
import {ThemeProvider} from "@/components/theme-provider.tsx";
import Root from "@/states/Root/Root.tsx";
import {ServerInfoProvider} from "@/contexts/ServerInfoContext.tsx";
import {Toaster} from "@/components/ui/toaster.tsx";

const App = () => {
    const [translationsLoaded, setTranslationsLoaded] = useState(false);

    const router = createBrowserRouter([
        {path: "/login", element: <Login />},
        {path: "/", element: <Root />, children: getRoutes()}
    ]);

    i18n.on("initialized", () => setTranslationsLoaded(true));

    if (!translationsLoaded) return <div>Loading...</div>;

    return (
        <ThemeProvider defaultTheme="dark" storageKey="theme">
            <ServerInfoProvider>
                <Toaster />
                <RouterProvider router={router}/>
            </ServerInfoProvider>
        </ThemeProvider>
    );
};

export default App;

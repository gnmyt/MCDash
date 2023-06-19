import {Dashboard, Extension, Folder, Group, Public, Settings, Terminal} from "@mui/icons-material";
import Overview from "@/states/Root/pages/Overview";
import {BanListProvider} from "@/states/Root/pages/Players/components/PlayerTable/contexts/BanList";
import Players from "@/states/Root/pages/Players";
import Files from "@/states/Root/pages/Files";

export const routes = [
    {path: "/", element: <Overview />},
    {path: "/players", element: <BanListProvider><Players /></BanListProvider>},
    {path: "/files/*", element: <Files />},
    {path: "/console", element: <h2>Console</h2>},
    {path: "/plugins", element: <h2>Plugins</h2>},
    {path: "/worlds", element: <h2>Worlds</h2>,},
    {path: "/configuration", element: <h2>Configuration</h2>,}
]

export const sidebar = [
    {
        path: "/",
        icon: <Dashboard />,
        name: "Overview"
    },
    {
        path: "/players",
        icon: <Group />,
        name: "Players"
    },
    {
        path: "/files",
        icon: <Folder />,
        name: "File Manager"
    },
    {
        path: "/console",
        icon: <Terminal />,
        name: "Console"
    },
    {
        path: "/plugins",
        icon: <Extension />,
        name: "Plugins"
    },
    {
        path: "/worlds",
        icon: <Public />,
        name: "Worlds"
    },
    {
        path: "/configuration",
        icon: <Settings />,
        name: "Configuration"
    }
]